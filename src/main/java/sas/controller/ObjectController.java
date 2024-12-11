package sas.controller;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import ch.qos.logback.core.joran.spi.ConsoleTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import sas.boundary.*;

@RestController
@RequestMapping(path = {"/aii/objects"})
public class ObjectController {
	private String springApplicationName;
	private Map<ObjectId, ObjectBoundary> dbMockup;
	private AtomicLong nextID;

	public ObjectController(){
		this.dbMockup = Collections.synchronizedMap(new HashMap<>());
		this.nextID = new AtomicLong(100L);
	}

	@Value("${spring.application.name:defaultAppName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
		System.err.println("********" + this.springApplicationName);
	}
	
	@GetMapping(
			path = {"/{systemID}/{id}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary getObjectById(
			@PathVariable("systemID") String systemID,
			@PathVariable("id") String id) {

		ObjectId objId = new ObjectId();
		objId.setId(id);
		objId.setSystemId(systemID);

		ObjectBoundary rv = this.dbMockup.get(objId);
		if (rv == null){
			throw new RuntimeException("Could not find Object by ID: " + objId);
		}

		System.err.println("*** " + rv);
		return rv;
	}

	@GetMapping(
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary[] getAllObjects() {

		ObjectBoundary[] rv = this.dbMockup
				.values()
				.toArray(new ObjectBoundary[0]);

		// for tests
		System.err.println("*** db length: " + this.dbMockup.size() + " , returned array size: " + rv.length);
		System.err.println("*** " + Arrays.toString(rv));
		return rv;
	}
	
	@PostMapping(
			consumes = {MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary insertObjectToDb(
			@RequestBody ObjectBoundary newObj){

		// create and set a new objectID for the new object
		ObjectId objectId = new ObjectId();
		objectId.setSystemId(this.springApplicationName);
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
		User user = cb.getUserId();
		if ( cb == null || user == null || user.getUserId() == null || user.getEmail() == null ){
			throw new RuntimeException("New object must contain a valid CreatedBy field - with a valid userID!");
		}

		// validate if objectDetails exist or should be created
		if ( newObj.getObjectDetails() == null ){
			newObj.setObjectDetails(new HashMap<>());
		}

		// INSERT newObj to db
		this.dbMockup.put(
				newObj.getObjectId(),
				newObj
		);

		return newObj;
	}


	@PutMapping(
			path = {"/{systemID}/{id}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject(
			@PathVariable("systemID") String systemID,
			@PathVariable("id") String id,
			@RequestBody ObjectBoundary update){
		ObjectId objId = new ObjectId(id, systemID);

		if ( this.dbMockup.containsKey(objId) ){
			boolean dirty = false;
			ObjectBoundary updatedObject = this.dbMockup.get(objId); // original object

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
				this.dbMockup.put(updatedObject.getObjectId(), updatedObject);
			}

		}else {
			throw new RuntimeException("Couldn't find any object with specified IDs");
		}
	}
}




