import requests
import random
import time
from datetime import datetime, timezone

# Constants
BASE_URL = "http://localhost:8081/aii/objects"
USER_EMAIL = "sensor@default.com"
SYSTEM_ID = "2025a.Liron.Barshishat"
HEADERS = {"Content-Type": "application/json"}

# Function to create plant objects
def create_plant_objects():
    object_ids = []

    for _ in range(5):
        payload = {
            "objectId": {
                "systemID": SYSTEM_ID,
                "id": "string"
            },
            "type": "Plant",
            "alias": "Flower",
            "status": "AVAILABLE",
            "location": {
                "lat": 0.8,
                "lng": 0.8
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
                "currentSoilMoistureLevel": 75,
                "optimalSoilMoistureLevel": 93,
                "currentLightLevelIntensity": 70,
                "optimalLightLevelIntensity": 100
            }
        }

        response = requests.post(BASE_URL, json=payload, headers=HEADERS)

        if response.status_code in range(200,300):
            data = response.json()
            full_id = f"{data['objectId']['systemID']}@@{data['objectId']['id']}"
            object_ids.append(full_id)
        else:
            print(f"Failed to create object: {response.status_code}, {response.text}")

    return object_ids

# Function to update plant objects
def update_plant_objects(object_ids):
    for full_id in object_ids:
        system_id, obj_id = full_id.split("@@")
        print(f"full id: {full_id}, system id: {system_id}, obj id: {obj_id}")
        payload = {
            "objectId": {
                "systemID": system_id,
                "id": obj_id
            },
            "type": "Plant",
            "alias": "Flower",
            "status": "AVAILABLE",
            "location": {
                "lat": 0.8,
                "lng": 0.8
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
                "currentSoilMoistureLevel": random.randint(0, 100),
                "optimalSoilMoistureLevel": 93,
                "currentLightLevelIntensity": random.randint(0, 100),
                "optimalLightLevelIntensity": 100,
                "relatedObjectId": full_id
            }
        }

        url = f"{BASE_URL}/{system_id}/{obj_id}?userSystemID={SYSTEM_ID}&userEmail={USER_EMAIL}"
        response = requests.put(url, json=payload, headers=HEADERS)

        if response.status_code == 200:
            print(f"Successfully updated object {obj_id}")
        else:
            print(f"Failed to update object {obj_id}: {response.status_code}, {response.text}")

# Main logic
def main():
    print("Creating plant objects...")
    object_ids = create_plant_objects()

    print("Created objects:", object_ids)

    if not object_ids:
        print("No objects created, exiting.")
    else:
        while True:
            print("Updating plant objects...")
            update_plant_objects(object_ids)
            time.sleep(15)

if __name__ == "__main__":
    main()
