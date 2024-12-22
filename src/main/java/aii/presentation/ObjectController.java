package aii.presentation;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import aii.logic.*;
import aii.logic.exceptions.InvalidInputException;
import aii.logic.exceptions.ObjectNotFoundException;

@RestController
public class ObjectController {
	private ObjectsService objects;

	public ObjectController(ObjectsService objects){
		this.objects = objects;
	}

	@GetMapping(
			path = {"/aii/objects/{systemID}/{id}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary getObjectById(
			@PathVariable("systemID") String systemID,
			@PathVariable("id") String id) {
		// TODO: get the userSystemID and userEmail and pass them to the service
		return this.objects.getSpecificObject("userSystemID","userEmail",systemID,id)
				.orElseThrow(() -> new ObjectNotFoundException("Couldn't find the object with object id - " + systemID + "@@" + id));
	}

	@GetMapping(
			path = {"/aii/objects/"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary[] getAllObjects() {
		ObjectBoundary[] rv = this.objects.getAll("userSystemID", "userEmail").toArray(new ObjectBoundary[0]);
		System.err.println("*** " + Arrays.toString(rv));
		return rv;
	}

	
	@PostMapping(
			path = {"/aii/objects"},
			consumes = {MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.CREATED)
	public ObjectBoundary insertObjectToDb(
			@RequestBody ObjectBoundary object){

		CreatedBy createdBy = object.getCreatedBy();
		if (createdBy == null){
			throw new InvalidInputException("createdBy can't be NULL!");
		}
		UserId userId = createdBy.getUserId();
		if (userId == null){
			throw new InvalidInputException("userId can't be NULL!");
		}

		String userSystemID = userId.getSystemID();
		String userEmail = userId.getEmail();
		if (userSystemID == null || userEmail == null || userSystemID.isBlank() || userEmail.isBlank()) {
			throw new InvalidInputException("userSystemID and userEmail can't be blank");
		}

		return this.objects.create(userSystemID, userEmail, object);
	}


	@PutMapping(
			path = {"/aii/objects/{systemID}/{id}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	public void updateObject(
			@PathVariable("systemID") String systemID,
			@PathVariable("id") String id,
				@RequestBody ObjectBoundary update){

			// TODO: get the userSystemID and userEmail and pass them to the service
			this.objects.update("userSystemID", "userEmail", systemID, id, update);
	}

	@DeleteMapping(path = {"/aii/admin/objects"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllObjects(){
		try{
			// TODO: get the adminSystemID and adminEmail and pass them to the service
			this.objects.deleteAllObjects("adminSystemID", "adminEmail"); 
		}catch(Exception e){
			System.err.println("[ERROR] - Something went wrong while trying to erase the objects Database.");
			throw new RuntimeException("Something went wrong while trying to erase the objects Database");	
		}
	}
}




