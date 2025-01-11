package aii.presentation;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aii.logic.CreatedBy;
import aii.logic.EnhancedObjectsService;
import aii.logic.ObjectBoundary;
import aii.logic.UserId;
import aii.logic.exceptions.InvalidInputException;
import aii.logic.exceptions.ObjectNotFoundException;
import aii.logic.exceptions.UserUnauthorizedException;

@RestController
public class ObjectController {
	private EnhancedObjectsService objects;

	public ObjectController(EnhancedObjectsService objects) {
		this.objects = objects;
	}

	@GetMapping(path = { "/aii/objects/{systemID}/{id}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ObjectBoundary getObjectById(
			@PathVariable("systemID") String systemID,
			@PathVariable("id") String id,
			@RequestParam(name = "userSystemID", required = true) String userSystemID,
			@RequestParam(name = "userEmail", required = true) String userEmail) {
		return this.objects.getSpecificObject(userSystemID, userEmail, systemID, id)
				.orElseThrow(() -> new ObjectNotFoundException(
						"Couldn't find the object with object id - " + systemID + "@@" + id));
	}

	@GetMapping(path = { "/aii/objects" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ObjectBoundary[] getAllObjects(
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "userSystemID", required = true) String systemID,
			@RequestParam(name = "userEmail", required = true) String email) {
		ObjectBoundary[] rv = this.objects.getAll(systemID, email, page, size).toArray(new ObjectBoundary[0]);
		System.err.println("*** " + Arrays.toString(rv));
		return rv;
	}

	@PostMapping(path = { "/aii/objects" }, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public ObjectBoundary insertObjectToDb(
			@RequestBody ObjectBoundary object) {

		CreatedBy createdBy = object.getCreatedBy();
		if (createdBy == null) {
			throw new InvalidInputException("createdBy can't be NULL!");
		}
		UserId userId = createdBy.getUserId();
		if (userId == null) {
			throw new InvalidInputException("userId can't be NULL!");
		}

		String userSystemID = userId.getSystemID();
		String userEmail = userId.getEmail();

		if (userSystemID == null || userEmail == null || userSystemID.isBlank() || userEmail.isBlank()) {
			throw new InvalidInputException("userSystemID and userEmail can't be blank");
		}

		return this.objects.create(userSystemID, userEmail, object);
	}

	@PutMapping(path = { "/aii/objects/{systemID}/{id}" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public void updateObject(
			@PathVariable("systemID") String systemID,
			@PathVariable("id") String id,
			@RequestBody ObjectBoundary update) {

		// TODO: get the userSystemID and userEmail and pass them to the service
		this.objects.update("userSystemID", "userEmail", systemID, id, update);
	}

	@DeleteMapping(path = { "/aii/admin/objects" })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllObjects(
			@RequestParam(name = "userSystemID", required = true) String systemID,
			@RequestParam(name = "userEmail", required = true) String email) {
		try {
			this.objects.deleteAllObjects(systemID, email);
		} catch (InvalidInputException e) {
			throw e;
		} catch (UserUnauthorizedException e) {
			throw e;
		} catch (Exception e) {
			System.err.println("[ERROR] - Something went wrong while trying to erase the objects Database.");
			throw new RuntimeException("Something went wrong while trying to erase the objects Database");
		}
	}

	@GetMapping(path = { "/aii/objects/search/byType/{type}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ObjectBoundary[] getObjectsByType(
			@PathVariable("type") String type,
			@RequestParam(name = "userSystemID", required = true) String systemID,
			@RequestParam(name = "userEmail", required = true) String email,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.objects
				.getObjectsByType(type, systemID, email, size, page)
				.toArray(new ObjectBoundary[0]);
	}

	@GetMapping(path = { "/aii/objects/search/byAlias/{alias}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ObjectBoundary[] getObjectsByAlias(
			@PathVariable("alias") String alias,
			@RequestParam(name = "userSystemID", required = true) String systemID,
			@RequestParam(name = "userEmail", required = true) String email,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.objects
				.getObjectsByAlias(alias, systemID, email, size, page)
				.toArray(new ObjectBoundary[0]);
	}


	@GetMapping(path = { "/aii/objects/search/byTypeAndStatus/{type}/{status}" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ObjectBoundary[] getObjectsByTypeAndStatus(
			@PathVariable("type") String type,
			@PathVariable("status") String status,
			@RequestParam(name = "userSystemID", required = true) String systemID,
			@RequestParam(name = "userEmail", required = true) String email,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.objects
				.getObjectsByTypeAndStatus(type, status, systemID, email, size, page)
				.toArray(new ObjectBoundary[0]);
	}

	@GetMapping(path = { "/aii/objects/search/byLocation/{lat}/{lng}/{distance}" })
	public ObjectBoundary[] getObjectsByLocation(
			@PathVariable("lat") double lat,
			@PathVariable("lng") double lng,
			@PathVariable("distance") double distance,
			@RequestParam(name = "units", required = false, defaultValue = "NEUTRAL") String distanceUnits,
			@RequestParam(name = "userSystemID", required = true) String systemID,
			@RequestParam(name = "userEmail", required = true) String email,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size) {

		return this.objects.getObjectsByLocation(lat, lng, distance, distanceUnits, systemID, email, page, size)
				.toArray(new ObjectBoundary[0]);
	}
}
