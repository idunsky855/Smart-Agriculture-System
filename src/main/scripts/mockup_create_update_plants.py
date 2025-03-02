import requests
import time
from datetime import datetime, timezone
import json

# Constants
BASE_URL = "http://localhost:8081/aii/objects"
USER_EMAIL = "sensor@default.com"
SYSTEM_ID = "2025a.Liron.Barshishat"
HEADERS = {"Content-Type": "application/json"}
LAT = -1.2921  # (Nairobi, Kenya)
LON = 36.8219  # (Nairobi, Kenya)

LAT = 25.2986
LON = 91.5822

PLANT_ALIASES = ["rose", "tulip", "daisy", "lily", "orchid"]


def get_plant_images(alias_of_plant, secrets):
    """
    This function retrieves plant images from Perenual API.
    It returns an image URL for the given plant alias.
    """

    search_url = f"{secrets['plant_images']['plant_images_url']}?key={secrets['plant_images']['plant_images_api_key']}&q={alias_of_plant}"

    try:
        # Make request to plant images API
        response = requests.get(search_url)

        if response.status_code == 200:
            data = response.json()

            if data['data']:
                # Get the first matching plant
                plant = data['data'][0]
                default_image = plant.get('default_image', {})

                # Check if the image exists
                image_url = default_image.get('original_url')

                if image_url:
                    print(f"Image URL: {image_url}")
                    return image_url
                else:
                    print(f"No image available for '{alias_of_plant}', returning None.")
            else:
                print(f"No matching plants found for '{alias_of_plant}', returning None.")
        else:
            print(f"Error: {response.status_code}, API response: {response.text}")

        return None

    except requests.RequestException as e:
        print(f"Error fetching plant images: {e}")
        return None

    except Exception as e:
        print(f"Something went wrong: {str(e)}")
        return None


# Function to create plant objects
def create_plant_objects(secrets):
    object_ids = []

    for i in range(5):
        payload = {
            "objectId": {
                "systemID": SYSTEM_ID,
                "id": "string"
            },
            "type": "Plant",
            "alias": f"Flower {i+1}",
            "status": "Flowering",
            "location": {
                "lat": LAT,
                "lng": LON
            },
            "active": True,
            "creationTimestamp": datetime.now(timezone.utc).isoformat(),
            "createdBy": {
                "userId": {
                    "systemID": SYSTEM_ID,
                    "email": USER_EMAIL
                }
            },
            "objectDetails": {
                "currentSoilMoistureLevel": 0,
                "optimalSoilMoistureLevel": 93,
                "currentLightLevelIntensity": 70,
                "optimalLightLevelIntensity": 100,
                "isRaining": False,
                "Image": get_plant_images(PLANT_ALIASES[i], secrets)
            }
        }

        response = requests.post(BASE_URL, json=payload, headers=HEADERS)

        if response.status_code in range(200, 300):
            data = response.json()
            full_id = f"{data['objectId']['systemID']}@@{data['objectId']['id']}"
            object_ids.append(full_id)
        else:
            print(f"Failed to create object: {response.status_code}, {response.text}")

    return object_ids


# Function to check weather
def check_weather(lat, lon, secrets):
    """
        This function will check the weather using the weatherapi.com API.
        The function will return the precipitation in mm.
        If an error occurs, the function will return -1.
    """

    # Prepare request parameters
    params = {
        "key": secrets['weather']['weather_api_key'],
        "q": f"{lat},{lon}"
    }

    try:
        # Make request to weather API
        response = requests.get(secrets['weather']['weather_url'], params=params)

        # Check for errors
        response.raise_for_status()

        # Parse response
        weather_data = response.json()

        # Debug:
        print(f"Weather API Response: {weather_data}")

        # Get precipitation
        precip_mm = weather_data["current"].get("precip_mm", 0)

        # Debug:
        print(f"Precipitation (mm): {precip_mm}")

        return precip_mm

    except requests.RequestException as e:
        print(f"Error checking weather: {e}")
        return -1


def update_plant_objects(object_ids, secrets):
    """
        This function will update the plant objects.
        The soilMoistureLevel will be increased by 5 if precipitation is detected.
        The soilMoistureLevel will not change if no precipitation is detected.
    """

    precip_mm = check_weather(LAT, LON, secrets)

    # If error checking weather, raise exception:
    if precip_mm == -1:
        raise Exception("Error checking weather")

    # Weather data is valid, continue with updating plant objects:
    for full_id in object_ids:

        # Split full id into system id and object id by the known delimiter that used in the server: "@@"
        system_id, obj_id = full_id.split("@@")

        # Debug:
        print(f"full id: {full_id}, system id: {system_id}, obj id: {obj_id}")

        # Fetch current state for updates
        url_get = f"{BASE_URL}/{system_id}/{obj_id}?userSystemID={SYSTEM_ID}&userEmail={USER_EMAIL}"

        # Get current data
        response_get = requests.get(url_get, headers=HEADERS)

        # If error fetching object, print error and skip to next object
        if response_get.status_code != 200:
            print(f"Failed to fetch object {obj_id}: {response_get.status_code}, {response_get.text}")
            continue

        # Object data is valid, continue with updating object:
        current_data = response_get.json()

        # Get current soil moisture level
        new_soil_moisture = current_data["objectDetails"]["currentSoilMoistureLevel"]

        # Get image URL:
        image_url = current_data["objectDetails"]["Image"]

        is_raining = False

        # Update soil moisture if precipitation > 0
        if precip_mm > 0:
            print(f"Rain detected, updating soil moisture. Current: {new_soil_moisture}, New: {new_soil_moisture + 5}")
            new_soil_moisture += 5

            # Update isRaining to True:
            is_raining = True
        else:
            print(f"No rain detected, updating soil moisture. Current: {new_soil_moisture}, New: {new_soil_moisture}")

        # Ensure soil moisture does not exceed 100
        new_soil_moisture = min(new_soil_moisture, 100)

        # Prepare payload for update
        payload = {
            "objectId": {
                "systemID": system_id,
                "id": obj_id
            },
            "type": "Plant",
            "location": {
                "lat": LAT,
                "lng": LON
            },
            "active": True,
            "creationTimestamp": datetime.now(timezone.utc).isoformat(),
            "createdBy": {
                "userId": {
                    "systemID": SYSTEM_ID,
                    "email": USER_EMAIL
                }
            },
            "objectDetails": {
                "currentSoilMoistureLevel": new_soil_moisture,
                "optimalSoilMoistureLevel": 93,
                "currentLightLevelIntensity": current_data["objectDetails"]["currentLightLevelIntensity"],
                "optimalLightLevelIntensity": 100,
                "relatedObjectId": full_id,
                "isRaining": is_raining,
                "Image": image_url
            }
        }

        # Update object
        url_put = f"{BASE_URL}/{system_id}/{obj_id}?userSystemID={SYSTEM_ID}&userEmail={USER_EMAIL}"
        response_put = requests.put(url_put, json=payload, headers=HEADERS)

        # Check if object was updated successfully
        if response_put.status_code == 200:
            print(f"Successfully updated object {obj_id}")
        else:
            print(f"Failed to update object {obj_id}: {response_put.status_code}, {response_put.text}")


def main():

    # Load secrets:
    try:
        secrets = json.load(open("credentials.json"))
    except FileNotFoundError:
        print("No credentials file found, exiting.")
        return
    except json.JSONDecodeError:
        print("Error reading credentials file, exiting.")
        return
    except Exception as e:
        print(f"Something went wrong: {str(e)}")
        return

    print("Creating plant objects...")
    object_ids = create_plant_objects(secrets)

    # Debug:
    print("Created objects:", object_ids)

    # If no objects created, exit:
    if not object_ids:
        print("No objects created, exiting.")
    else:
        while True:
            """
                Infinite loop to update plant objects.
                The plants will be updated every 30 seconds.
            """
            print("Updating plant objects...")
            update_plant_objects(object_ids, secrets)

            # Run every 30 seconds
            time.sleep(30)


if __name__ == "__main__":
    main()
