package aii.presentation;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import aii.logic.*;
import aii.logic.exceptions.ObjectNotFoundException;

@RestController
public class ObjectController {
	private ObjectsService objects;

	public ObjectController(ObjectsService objects){
		this.objects = objects;
	}

	@GetMapping(
			path = {"/aii/objects/{objectSystemID}/{objectId}/{userSystemID}/{userEmail}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary getObjectById(
			@PathVariable("objectSystemID") String objectSystemID,
			@PathVariable("objectId") String objectId,
			@PathVariable("userSystemID") String userSystemID,
			@PathVariable("userEmail") String userEmail) {

		return this.objects.getSpecificObject(userSystemID,userEmail,objectSystemID,objectId)
				.orElseThrow(() -> new ObjectNotFoundException("Couldn't find the object with object id - " + objectId));
	}

	@GetMapping(
			path = {"/aii/objects/{userSystemID}/{userEmail}"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary[] getAllObjects(
		@PathVariable("userSystemID") String userSystemID,
		@PathVariable("userEmail") String userEmail
	) {

		ObjectBoundary[] rv = this.objects.getAll(userSystemID, userEmail).toArray(new ObjectBoundary[0]);
		System.err.println("*** " + Arrays.toString(rv));
		return rv;
	}

	
	@PostMapping(
			path = {"/aii/objects/{userSystemID}/{userEmail}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public ObjectBoundary insertObjectToDb(
			@PathVariable("userSystemID") String userSystemID,
			@PathVariable("userEmail") String userEmail,
			@RequestBody ObjectBoundary object){

		return this.objects.create(userSystemID, userEmail, object);
	}


	@PutMapping(
			path = {"/aii/objects/{objectSystemID}/{objectId}/{userSystemID}/{userEmail}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject(
			@PathVariable("objectSystemID") String objectSystemID,
			@PathVariable("objectId") String objectId,
			@PathVariable("userSystemID") String userSystemID,
			@PathVariable("userEmail") String userEmail,
			@RequestBody ObjectBoundary update){

			this.objects.update(userSystemID, userEmail, objectSystemID, objectId, update);
	}

	@DeleteMapping(
			path = {"/aii/admin/objects/{adminSystemID}/{adminEmail}"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllObjects(
		@PathVariable("adminSystemID") String adminSystemID,
		@PathVariable("adminEmail") String adminEmail
	){
		try{
			this.objects.deleteAllObjects(adminSystemID, adminEmail); 
		}catch(Exception e){
			System.err.println("[ERROR] - Something went wrong while trying to erase the objects Database.");
			throw new RuntimeException("Something went wrong while trying to erase the objects Database");	
		}
	}
}




