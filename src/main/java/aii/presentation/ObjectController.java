package aii.presentation;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import aii.logic.*;

@RestController
public class ObjectController {
	private String springApplicationName;
	private Map<ObjectId, ObjectBoundary> objectsDb;
	private AtomicLong nextID;

	public ObjectController(){
		this.objectsDb = Collections.synchronizedMap(new HashMap<>());
		this.nextID = new AtomicLong(1L);
	}

	@Value("${spring.application.name:defaultAppName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
		System.err.println("********" + this.springApplicationName);
	}
	@GetMapping(
			path = {"/aii/objects/{systemID}/{id}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary getObjectById(
			@PathVariable("systemID") String systemID,
			@PathVariable("id") String id) {

		// check if any of the ID's are either empty or only whitespace
		if ( systemID.isBlank() || id.isBlank() ) {
			throw new RuntimeException("systemID and id can't be blank");
		}

		ObjectId objId = new ObjectId();
		objId.setId(id);
		objId.setSystemID(systemID);

		ObjectBoundary rv = this.objectsDb.get(objId);
		if (rv == null){
			throw new RuntimeException("Could not find Object by ID: " + objId);
		}

		System.err.println("*** " + rv);
		return rv;
	}

	@GetMapping(
			path = {"/aii/objects"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary[] getAllObjects() {

		ObjectBoundary[] rv = this.objectsDb
				.values()
				.toArray(new ObjectBoundary[0]);

		// for tests
		System.err.println("*** db length: " + this.objectsDb.size() + " , returned array size: " + rv.length);
		System.err.println("*** " + Arrays.toString(rv));
		return rv;
	}

	@PostMapping(
			path = {"/aii/objects"},
			consumes = {MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public ObjectBoundary insertObjectToDb(
			@RequestBody ObjectBoundary newObj){

		// create and set a new objectID for the new object
		ObjectId objectId = new ObjectId();
		objectId.setSystemID(this.springApplicationName);
		objectId.setId("" + this.nextID.getAndIncrement());
		newObj.setObjectId(objectId);

		// validate type
		if ( newObj.getType() == null ){
			throw new RuntimeException("New objects must contain a type value!");
		}

		// validate status
		if ( newObj.getStatus() == null ){
			newObj.setStatus("UNAVAILABLE");
		}

		// validate location and set to default if invalid
		Location objLocation = newObj.getLocation();
		if ( objLocation == null || objLocation.getLat() == null || objLocation.getLng() == null ){
			// if no valid location was given - set the location to Google's Headquarters - Mountain View, California
			newObj.setLocation(new Location(37.4220,-122.0841));
		}

		// validate active
		if ( newObj.getActive() == null ){
			// default is active = false
			newObj.setActive(false);
		}

		// set creation timestamp - now
		newObj.setCreationTimestamp(new Date());

		// validate created by
		CreatedBy cb = newObj.getCreatedBy();
		UserId user = cb.getUserId();
		if ( cb == null || user == null || user.getSystemID() == null || user.getEmail() == null ){
			throw new RuntimeException("New object must contain a valid CreatedBy field - with a valid userID!");
		}

		// validate if objectDetails exist or should be created
		if ( newObj.getObjectDetails() == null ){
			newObj.setObjectDetails(new HashMap<>());
		}

		// INSERT newObj to db
		this.objectsDb.put(
				newObj.getObjectId(),
				newObj
		);

		return newObj;
	}


	@PutMapping(
			path = {"/aii/objects/{systemID}/{id}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject(
			@PathVariable("systemID") String systemID,
			@PathVariable("id") String id,
			@RequestBody ObjectBoundary update){

		// check if any of the ID's are either empty or only whitespace
		if ( systemID.isBlank() || id.isBlank() ) {
			throw new RuntimeException("systemID and id can't be blank");
		}

		ObjectId objId = new ObjectId(id, systemID);

		if ( this.objectsDb.containsKey(objId) ){
			boolean dirty = false;
			ObjectBoundary updatedObject = this.objectsDb.get(objId); // original object
			
			// if type updated
			if (update.getType() != null && !update.getType().isBlank() ) {
				updatedObject.setType(update.getType());
				dirty = true;
			}

			// if alias updated
			if ( update.getAlias() != null ){
				updatedObject.setAlias(update.getAlias());
				dirty = true;
			}

			// if status updated
			if ( update.getStatus() != null ){
				updatedObject.setStatus(update.getStatus());
				dirty = true;
			}

			// if location updated
			if ( update.getLocation() != null ){
				Location newLoc = update.getLocation();
				if ( newLoc.getLng() != null && newLoc.getLat() != null ){
					updatedObject.setLocation(newLoc);
					dirty = true;
				}
			}

			// if active updated
			if ( update.getActive() != null ) {
				updatedObject.setActive(update.getActive());
				dirty = true;
			}

			// if object details updated
			if ( update.getObjectDetails() != null ){
				// add or update all objectDetails entries
				updatedObject.getObjectDetails().putAll(update.getObjectDetails());
				dirty = true;
			}

			if (dirty){
				// update
				this.objectsDb.put(updatedObject.getObjectId(), updatedObject);
			}

		}else {
			throw new RuntimeException("Couldn't find any object with specified IDs");
		}
	}

	@DeleteMapping(
			path = {"/aii/admin/objects"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllObjects(){
		this.objectsDb.clear();
	}
}




