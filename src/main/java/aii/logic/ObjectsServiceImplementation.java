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
import aii.logic.converters.ObjectConverter;
import aii.logic.exceptions.InvalidInputException;
import aii.logic.exceptions.ObjectNotFoundException;
import aii.logic.exceptions.UserUnauthorizedException;
import aii.logic.utilities.EmailValidator;

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

		UserRole role = users.getUserRole(userSystemID, userEmail);

		if (role != UserRole.OPERATOR) {
			throw new UserUnauthorizedException("User is not authorized to create objects!");
		}

		// Passed validaitons - create id for the new object:
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

		if (!emailValidator.isEmailValid(userEmail)) {
			throw new InvalidInputException("Invalid email format");
		}

		UserRole role = users.getUserRole(userSystemID, userEmail);

		if (role != UserRole.OPERATOR) {
			throw new UserUnauthorizedException("User is not authorized to update objects!");
		}

		if (!entityOp.isEmpty()) {

			ObjectEntity updatedObject = entityOp.get(); // original object

			// if type updated - can't be blank
			if (update.getType() != null && !update.getType().isBlank()) {
				updatedObject.setType(update.getType());
			}

			// if alias updated - can't be blank
			if (update.getAlias() != null && !update.getAlias().isBlank()) {
				updatedObject.setAlias(update.getAlias());
			}
			

			// if status updated - can't be blank 
			if (update.getStatus() != null && !update.getStatus().isBlank()) {
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
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ObjectBoundary> getSpecificObject(String userSystemID, String userEmail, String objectSystemID, String objectId) {
		// check if any of the ID's are either empty or only whitespace
		if (objectSystemID == null || objectId == null || objectSystemID.isBlank() || objectId.isBlank()) {
			throw new InvalidInputException("objectSystemID and objectId can't be blank");
		}

		if (userSystemID == null || userEmail == null || userSystemID.isBlank() || userEmail.isBlank()) {
			throw new InvalidInputException("userSystemID and userEmail can't be blank");
		}

		UserRole role = users.getUserRole(userSystemID, userEmail);
		String objectKey = objectSystemID + "@@" + objectId;

		switch (role) {
		case ADMIN:
			throw new UserUnauthorizedException("Get a specific object by ID is unauthorized for admin users!");
		case END_USER:
			return this.objects.findByObjectIdAndActiveTrue(objectKey).map(this.converter::toBoundary);
		case OPERATOR:
			return this.objects.findById(objectKey).map(this.converter::toBoundary);
		default:
			throw new IllegalArgumentException("Unexpected value: " + role);
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
					.stream().map(this.converter::toBoundary).toList();
		} else if (role == UserRole.OPERATOR) {
			rv = this.objects.findAll(PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId")).stream()
					.map(this.converter::toBoundary).toList();
		} else {
			throw new UserUnauthorizedException("User is not authorized to view all objects!");
		}
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByLocation(double lat, double lng, double distance, String distanceUnits, String userSystemID, String userEmail, int page, int size) {
		if (distance < 0.0) {
			throw new InvalidInputException("Distance must be positive!");
		}

		if (!distanceUnits.equals("NEUTRAL") && !distanceUnits.equals("MILES") && !distanceUnits.equals("KILOMETERS")){
			throw new InvalidInputException("Distance Units must be one of NEUTRAL, KILOMETERS and MILES!");
		}

		UserRole role = users.getUserRole(userSystemID, userEmail);
		switch (role) {
			case UserRole.END_USER:
				// find only active objects
				// already sorted by query
				return this.objects
						.findAllWithinRadiusAndActiveIsTrue(lat, lng, distance, distanceUnits, PageRequest.of(page, size))
						.stream()
						.map(this.converter::toBoundary)
						.toList();

			case UserRole.OPERATOR:
				// find all objects
				// already sorted by query
				return this.objects
						.findAllWithinRadius(lat, lng, distance, distanceUnits, PageRequest.of(page, size))
						.stream()
						.map(this.converter::toBoundary)
						.toList();

			default:
				throw new UserUnauthorizedException(
						"User is not authorized to view objects by location!");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByType(String type, String userSystemID, String userEmail, int size, int page) {

		UserRole role = users.getUserRole(userSystemID, userEmail);

		switch (role) {
		case ADMIN:
			throw new UserUnauthorizedException("Searching an object by type is unauthorized for admin users!");
		case END_USER:
			return this.objects
					.findAllByTypeAndActiveTrue(type,
							PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
					.stream().map(this.converter::toBoundary).toList();
		case OPERATOR:
			return this.objects
					.findAllByType(type, PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
					.stream().map(this.converter::toBoundary).toList();
		default:
			throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByTypeAndStatus(String type, String status, String userSystemID,
			String userEmail, int size, int page) {

		UserRole role = users.getUserRole(userSystemID, userEmail);

		switch (role) {
		case ADMIN:
			throw new UserUnauthorizedException(
					"Searching an object by type and status is unauthorized for admin users!");
		case END_USER:
			return this.objects
					.findAllByTypeAndStatusAndActiveTrue(type, status,
							PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
					.stream().map(this.converter::toBoundary).toList();
		case OPERATOR:
			return this.objects
					.findAllByTypeAndStatus(type, status,
							PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
					.stream().map(this.converter::toBoundary).toList();
		default:
			throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	@Override
    @Transactional(readOnly = true)
    public List<ObjectBoundary> getObjectsByAlias(String alias, String userSystemID, String userEmail, int size, int page) {
            UserRole role = users.getUserRole(userSystemID, userEmail);

        switch (role) {
            case ADMIN:
                throw new UserUnauthorizedException("Searching an object by alias is unauthorized for admin users!");

            case END_USER:
                return this.objects
                        .findAllByAliasAndActiveTrue(alias,
                                PageRequest.of(page, size, Direction.ASC, "creationTime", "objectId"))
                        .stream().map(this.converter::toBoundary).toList();

            case OPERATOR:
                return this.objects
                        .findAllByAlias(alias,
                                PageRequest.of(page, size, Direction.ASC, "creationTime", "objectId"))
                        .stream().map(this.converter::toBoundary).toList();
            default:
                throw new IllegalArgumentException("Unexpected value: " + role);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<ObjectBoundary> getObjectsByAliasPattern(String pattern, String userSystemID, String userEmail, int size, int page) {

		UserRole role = users.getUserRole(userSystemID, userEmail);

        switch (role) {
            case ADMIN:
                throw new UserUnauthorizedException("Searching an object by alias pattern is unauthorized for admin users!");

            case END_USER:
                return this.objects
                        .findAllByAliasLikeAndActiveTrue("%" + pattern + "%",
                                PageRequest.of(page, size, Direction.ASC, "creationTime", "objectId"))
                        .stream().map(this.converter::toBoundary).toList();

            case OPERATOR:
                return this.objects
                        .findAllByAliasLike("%" + pattern + "%",
                                PageRequest.of(page, size, Direction.ASC, "creationTime", "objectId"))
                        .stream().map(this.converter::toBoundary).toList();
            default:
                throw new IllegalArgumentException("Unexpected value: " + role);
        }
    }

}
