package aii.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aii.dal.ObjectsCrud;
import aii.data.ObjectEntity;
import aii.data.UserRole;
import aii.logic.exceptions.InvalidInputException;
import aii.logic.exceptions.ObjectNotFoundException;
import aii.logic.exceptions.UserUnauthorizedException;
import aii.logic.utilities.EmailValidator;
import aii.logic.converters.ObjectConverter;

@Service
public class ObjectsServiceImplementation implements EnhancedObjectsService {
	private ObjectsCrud objects;
	private String springApplicationName;
	private ObjectConverter converter;
	private EmailValidator emailValidator;
	private EnhancedUsersService users;
	private final Float MILES_TO_KMS = 1.609344f;

	public ObjectsServiceImplementation(ObjectsCrud objects, ObjectConverter converter, EnhancedUsersService users) {
		this.objects = objects;
		this.converter = converter;
		this.emailValidator = new EmailValidator();
		this.users = users;
	}

	@Value("${spring.application.name:defaultAppName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
		// log debug
		System.out.println("[DEBUG] - ObjectsServiceImplementation " + this.springApplicationName);
		System.out.println("[DEBUG] - ObjectsService " + this.springApplicationName);
	}

	@Override
	@Transactional
	public ObjectBoundary create(String userSystemID, String userEmail, ObjectBoundary object) {

		if (object == null) {
			throw new InvalidInputException("object can't be NULL!");
		}
		if (userSystemID == null || userEmail == null || userSystemID.isBlank() || userEmail.isBlank()) {
			throw new InvalidInputException("userSystemID and userEmail can't be blank");
		}

		// create id for the new object
		ObjectId objectId = new ObjectId(UUID.randomUUID().toString(), this.springApplicationName);
		object.setObjectId(objectId);

		// validate type
		if (object.getType() == null || object.getType().isBlank()) {
			throw new InvalidInputException("New objects must contain a type value!");
		}

		// validate if alias is blank - replace with null
		if (object.getAlias() == null || object.getAlias().isBlank()) {
			throw new InvalidInputException("New objects must contain an alias value!");
		}

		// validate status
		if (object.getStatus() == null || object.getStatus().isBlank()) {
			throw new InvalidInputException("New objects must contain a status value!");
		}

		// if object location is null - set it to a new one
		if (object.getLocation() == null) {
			object.setLocation(new Location());
		}
		// validate location and set to default if invalid
		Location objLocation = object.getLocation();
		if (objLocation.getLat() == null || objLocation.getLng() == null) {
			// if no valid location was given - set the location to Google's Headquarters -
			// Mountain View, California
			object.setLocation(new Location(37.4220, -122.0841));
		}

		// validate active
		if (object.getActive() == null) {
			// default is active = false
			object.setActive(false);
		}

		// set creation timestamp - now
		object.setCreationTimestamp(new Date());

		// create and validate created by
		if (object.getCreatedBy() == null) {
			throw new InvalidInputException("New object must contain a valid CreatedBy field - with a valid userID!");
		}
		CreatedBy cb = object.getCreatedBy();
		if (cb.getUserId() == null || cb.getUserId().getEmail() == null || cb.getUserId().getEmail().isBlank()
				|| cb.getUserId().getSystemID() == null || cb.getUserId().getSystemID().isBlank()
				|| !emailValidator.isEmailValid(cb.getUserId().getEmail())) {
			throw new InvalidInputException("New object must contain a valid CreatedBy field - with a valid userID!");
		}

		// validate if objectDetails exist or should be created
		if (object.getObjectDetails() == null) {
			object.setObjectDetails(new HashMap<>());
		}
		// INSERT object to db
		return this.converter.toBoundary(this.objects.save(this.converter.toEntity(object)));
	}

	@Override
	@Transactional
	public ObjectBoundary update(String userSystemID, String userEmail, String objectSystemID, String objectId,
			ObjectBoundary update) {

		Optional<ObjectEntity> entityOp = this.objects.findById(objectSystemID + "@@" + objectId);

		userSystemID = userSystemID.trim();
		userEmail = userEmail.trim();
		objectSystemID = objectSystemID.trim();
		objectId = objectId.trim();

		// check if any of the ID's are either empty or only whitespace
		if (objectSystemID == null || objectId == null || objectSystemID.isBlank() || objectId.isBlank()) {
			throw new InvalidInputException("objectSystemID and objectId can't be blank");
		}

		if (userSystemID == null || userEmail == null || userSystemID.isBlank() || userEmail.isBlank()) {
			throw new InvalidInputException("userSystemID and userEmail can't be blank");
		}

		if (!entityOp.isEmpty()) {

			ObjectEntity updatedObject = entityOp.get(); // original object

			// if type updated
			if (update.getType() != null && !update.getType().isBlank()) {
				updatedObject.setType(update.getType());
			}
			// if alias updated
			if (update.getAlias() != null) {
				updatedObject.setAlias(update.getAlias());
			}
			// if status updated
			if (update.getStatus() != null) {
				updatedObject.setStatus(update.getStatus());
			}

			// if location updated
			if (update.getLocation() != null) {
				Location newLoc = update.getLocation();
				if (newLoc.getLng() != null && newLoc.getLat() != null) {
					updatedObject.setLocation(newLoc);
				}
			}

			// if active updated
			if (update.getActive() != null) {
				updatedObject.setActive(update.getActive());
			}

			// if object details updated
			if (update.getObjectDetails() != null) {
				// add or update all objectDetails entries
				updatedObject.setObjectDetails(update.getObjectDetails());
			}

			return this.converter.toBoundary(this.objects.save(updatedObject));

		} else {

			throw new ObjectNotFoundException("Couldn't find the object with object id - " + objectId);
		}
	}

	@Override
	@Deprecated
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getAll(String userSystemID, String userEmail) {
		throw new RuntimeException("Deprecated operation - use getAll that uses pagination");
		// if (userSystemID == null || userEmail == null || userSystemID.isBlank() ||
		// userEmail.isBlank()) {
		// throw new InvalidInputException("userSystemID and userEmail can't be blank");
		// }
		// return this.objects.findAll()
		// .stream()
		// .map(this.converter::toBoundary)
		// .toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ObjectBoundary> getSpecificObject(String userSystemID, String userEmail, String objectSystemID,
			String objectId) {
		// check if any of the ID's are either empty or only whitespace
		if (objectSystemID == null || objectId == null || objectSystemID.isBlank() || objectId.isBlank()) {
			throw new InvalidInputException("objectSystemID and objectId can't be blank");
		}

		if (userSystemID == null || userEmail == null || userSystemID.isBlank() || userEmail.isBlank()) {
			throw new InvalidInputException("userSystemID and userEmail can't be blank");
		}

		try {
			UserRole role = users.getUserRole(userSystemID, userEmail);

			switch (role) {
				case ADMIN:
					throw new UserUnauthorizedException("Get a specific object by ID is unauthorized for admin users!");
				case END_USER:
					return this.objects.findById(objectSystemID + "@@" + objectId)
							.filter(entity -> entity.getActive()) // Check if the object is active
							.map(this.converter::toBoundary)
							.or(() -> {
								throw new ObjectNotFoundException(
										"Couldn't find the object with object id - " + objectId);
							});
				case OPERATOR:
					return this.objects.findById(objectSystemID + "@@" + objectId).map(this.converter::toBoundary);
				default:
					throw new IllegalArgumentException("Unexpected value: " + role);
			}

		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	@Transactional
	public void deleteAllObjects(String adminSystemID, String adminEmail) {
		if (users.getUserRole(adminSystemID, adminEmail) == UserRole.ADMIN) {
			this.objects.deleteAll();
			return;
		}
		throw new UserUnauthorizedException("Only Admins are authorized to delete all objects!");
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getAll(String userSystemID, String userEmail, int page, int size) {

		List<ObjectBoundary> rv = null;
		UserRole role = users.getUserRole(userSystemID, userEmail);
		if (role == UserRole.END_USER) {
			rv = this.objects
					.findAllByActiveTrue(PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
					.stream()
					.map(this.converter::toBoundary)
					.toList();
		} else if (role == UserRole.OPERATOR) {
			rv = this.objects
					.findAll(PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
					.stream()
					.map(this.converter::toBoundary)
					.toList();
		} else {
			throw new UserUnauthorizedException("Only operators and end users are allowed to use this!");
		}
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByLocation(double lat, double lng, double distance, String distanceUnits,
			String userSystemID, String userEmail, int page, int size) {
		if (distance < 0.0) {
			throw new InvalidInputException("Distance must be positive!");
		}
		UserRole role = users.getUserRole(userSystemID, userEmail);
		switch (role) {
			case UserRole.END_USER:
				// find only active objects
				if (distanceUnits.equalsIgnoreCase("neutral")) {
					// already sorted by query
					return this.objects
							.findAllWithinRadiusAndActiveIsTrue(lat, lng, distance, PageRequest.of(page, size))
							.stream()
							.map(this.converter::toBoundary)
							.toList();
				} else {
					// already sorted by query
					// convert to kms for calculation
					if (distanceUnits.equalsIgnoreCase("miles"))
						distance *= MILES_TO_KMS;
					return this.objects
							.findAllWithinRadiusKmAndActiveIsTrue(lat, lng, distance, PageRequest.of(page, size))
							.stream()
							.map(this.converter::toBoundary)
							.toList();
				}
			case UserRole.OPERATOR:
				// find all objects
				if (distanceUnits.equalsIgnoreCase("neutral")) {
					// already sorted by query
					return this.objects
							.findAllWithinRadius(lat, lng, distance, PageRequest.of(page, size))
							.stream()
							.map(this.converter::toBoundary)
							.toList();
				} else {
					// already sorted by query
					// convert to kms for calculation
					if (distanceUnits.equalsIgnoreCase("miles"))
						distance *= MILES_TO_KMS;
					return this.objects
							.findAllWithinRadiusKm(lat, lng, distance, PageRequest.of(page, size))
							.stream()
							.map(this.converter::toBoundary)
							.toList();
				}
			default:
				throw new UserUnauthorizedException(
						"Only end users and operators are authorized searching by location!");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByType(String type, String userSystemID, String userEmail, int size,
			int page) {
		try {
			UserRole role = users.getUserRole(userSystemID, userEmail);

			switch (role) {
				case ADMIN:
					throw new UserUnauthorizedException("Searching an object by type is unauthorized for admin users!");
				case END_USER:
					return this.objects
							.findAllByTypeIgnoreCaseAndActiveTrue(type,
									PageRequest.of(page, size, Direction.ASC, "type", "objectId"))
							.stream().map(this.converter::toBoundary).toList();
				case OPERATOR:
					return this.objects
							.findAllByTypeIgnoreCase(type,
									PageRequest.of(page, size, Direction.ASC, "type", "objectId"))
							.stream().map(this.converter::toBoundary).toList();
				default:
					throw new IllegalArgumentException("Unexpected value: " + role);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByTypeAndStatus(String type, String status, String userSystemID,
			String userEmail, int size, int page) {
		try {
			UserRole role = users.getUserRole(userSystemID, userEmail);

			switch (role) {
				case ADMIN:
					throw new UserUnauthorizedException(
							"Searching an object by type and status is unauthorized for admin users!");
				case END_USER:
					return this.objects
							.findAllByTypeAndStatusIgnoreCaseAndActiveTrue(type, status,
									PageRequest.of(page, size, Direction.ASC, "type", "status", "objectId"))
							.stream().map(this.converter::toBoundary).toList();
				case OPERATOR:
					return this.objects
							.findAllByTypeAndStatusIgnoreCase(type, status,
									PageRequest.of(page, size, Direction.ASC, "type", "status", "objectId"))
							.stream().map(this.converter::toBoundary).toList();
				default:
					throw new IllegalArgumentException("Unexpected value: " + role);
			}

		} catch (Exception e) {
			throw e;
		}
	}

}
