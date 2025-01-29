package aii.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private Log logger = LogFactory.getLog(ObjectsServiceImplementation.class);


	public ObjectsServiceImplementation(ObjectsCrud objects, ObjectConverter converter, EnhancedUsersService users) {
		this.objects = objects;
		this.converter = converter;
		this.emailValidator = new EmailValidator();
		this.users = users;
	}

	@Value("${spring.application.name:defaultAppName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.logger.trace("setSpringApplicationName(" + springApplicationName + ")");
		this.springApplicationName = springApplicationName;

		// Log:
		this.logger.debug("ObjectServiceImplementation" + this.springApplicationName);
	}

	@Override
	@Transactional
	public ObjectBoundary create(String userSystemID, String userEmail, ObjectBoundary object) {
		this.logger.trace("create(" + userSystemID + ", " + userEmail + ", " + object + ")");

		if (object == null) {
			this.logger.error("Object is null");
			throw new InvalidInputException("object can't be NULL!");
		}

		UserRole role = users.getUserRole(userSystemID, userEmail);

		this.logger.debug("UserRole: " + role.toString());

		if (role != UserRole.OPERATOR) {
			this.logger.error("User is not authorized to create objects");
			throw new UserUnauthorizedException("User is not authorized to create objects!");
		}

		// Passed validaitons - create id for the new object:
		ObjectId objectId = new ObjectId(UUID.randomUUID().toString(), this.springApplicationName);

		this.logger.debug("ObjectId: " + objectId.toString());

		object.setObjectId(objectId);

		// validate type
		if (object.getType() == null || object.getType().isBlank()) {
			this.logger.error("New objects must contain a type value!");
			throw new InvalidInputException("New objects must contain a type value!");
		}

		// validate if alias is blank - replace with null
		if (object.getAlias() == null || object.getAlias().isBlank()) {
			this.logger.error("New objects must contain an alias value!");
			throw new InvalidInputException("New objects must contain an alias value!");
		}

		// validate status
		if (object.getStatus() == null || object.getStatus().isBlank()) {
			this.logger.error("New objects must contain a status value!");
			throw new InvalidInputException("New objects must contain a status value!");
		}

		// if object location is null - set it to a new one
		if (object.getLocation() == null) {
			this.logger.warn("New object location is null - setting to default location!");
			object.setLocation(new Location());
		}

		// validate location and set to default if invalid
		Location objLocation = object.getLocation();
		if (objLocation.getLat() == null || objLocation.getLng() == null) {
			// if no valid location was given - set the location to Google's Headquarters -
			// Mountain View, California

			this.logger.warn("New object location is invalid - setting to default location! (Google HQ)");
			object.setLocation(new Location(37.4220, -122.0841));
		}

		// validate active
		if (object.getActive() == null) {
			this.logger.warn("New object active status is null - default is false");

			// default is active = false
			object.setActive(false);
		}

		// set creation timestamp - now
		object.setCreationTimestamp(new Date());

		// create and validate created by
		if (object.getCreatedBy() == null) {
			this.logger.error("New object must contain a valid CreatedBy field - with a valid userID!");
			throw new InvalidInputException("New object must contain a valid CreatedBy field - with a valid userID!");
		}
		CreatedBy cb = object.getCreatedBy();
		if (cb.getUserId() == null || cb.getUserId().getEmail() == null || cb.getUserId().getEmail().isBlank()
				|| cb.getUserId().getSystemID() == null || cb.getUserId().getSystemID().isBlank()
				|| !emailValidator.isEmailValid(cb.getUserId().getEmail())) {
			this.logger.error("New object must contain a valid CreatedBy field - with a valid userID!");
			throw new InvalidInputException("New object must contain a valid CreatedBy field - with a valid userID!");
		}

		// validate if objectDetails exist or should be created
		if (object.getObjectDetails() == null) {
			object.setObjectDetails(new HashMap<>());
		}else{
			Map<String, Object> details = object.getObjectDetails();
			Set<String> keys = details.keySet();

			if (keys.contains("relatedObjectId") &&
						details.get("relatedObjectId") != null) {

					String relatedObjectId = details.get("relatedObjectId").toString();

					if (!relatedObjectId.isBlank()) {

						Optional<ObjectBoundary> op = this.objects.findById(relatedObjectId)
								.map(this.converter::toBoundary);
						if (op.isEmpty()) {
							this.logger.error("Invalid relatedObjectId!");
							throw new InvalidInputException("Invalid relatedObjectId!");
						}
					}
				}
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
			this.logger.error("objectSystemID and objectId can't be blank");
			throw new InvalidInputException("objectSystemID and objectId can't be blank");
		}

		if (!emailValidator.isEmailValid(userEmail)) {
			this.logger.error("Invalid email format");
			throw new InvalidInputException("Invalid email format");
		}

		UserRole role = users.getUserRole(userSystemID, userEmail);

		this.logger.debug("UserRole: " + role.toString());

		if (role != UserRole.OPERATOR) {
			this.logger.error("User is not authorized to update objects");
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

				if (newLoc.getLat() != null){
					updatedObject.setLat(newLoc.getLat());
				}

				if (newLoc.getLng() != null) {
					updatedObject.setLng(newLoc.getLng());
				}
			}

			// if active updated
			if (update.getActive() != null) {
				updatedObject.setActive(update.getActive());
			}

			// if object details updated
			if (update.getObjectDetails() != null) {
				// add or update all objectDetails entries
				Map<String, Object> details = update.getObjectDetails();
				Set<String> keys = details.keySet();

				if (keys.contains("currentSoilMoistureLevel") &&
						details.get("currentSoilMoistureLevel") != null &&
						details.get("currentSoilMoistureLevel") instanceof Integer) {

					int value = (int) details.get("currentSoilMoistureLevel");
					if (value <= 100 && value >= 0)
						updatedObject.setCurrentSoilMoistureLevel(value);
				}
				details.remove("currentSoilMoistureLevel"); // for optimal storage in db

				if (keys.contains("optimalSoilMoistureLevel") &&
						details.get("optimalSoilMoistureLevel") != null &&
						details.get("optimalSoilMoistureLevel") instanceof Integer) {

					int value = (int) details.get("optimalSoilMoistureLevel");
					if (value <= 100 && value >= 0)
						updatedObject.setOptimalSoilMoistureLevel(value);
				}
				details.remove("optimalSoilMoistureLevel"); // for optimal storage in db

				if (keys.contains("currentLightLevelIntensity") &&
						details.get("currentLightLevelIntensity") != null &&
						details.get("currentLightLevelIntensity") instanceof Integer) {

					int value = (int) details.get("currentLightLevelIntensity");
					if (value <= 100 && value >= 0)
						updatedObject.setCurrentLightLevelIntensity(value);
				}
				details.remove("currentLightLevelIntensity"); // for optimal storage in db

				if (keys.contains("optimalLightLevelIntensity") &&
						details.get("optimalLightLevelIntensity") != null &&
						details.get("optimalLightLevelIntensity") instanceof Integer) {

					int value = (int) details.get("optimalLightLevelIntensity");
					if (value <= 100 && value >= 0)
						updatedObject.setOptimalLightLevelIntensity(value);
				}
				details.remove("optimalLightLevelIntensity"); // for optimal storage in db

				if (keys.contains("relatedObjectId") &&
						details.get("relatedObjectId") != null) {

					String relatedObjectId = details.get("relatedObjectId").toString();

					if (!relatedObjectId.isBlank()) {

						Optional<ObjectBoundary> op = this.objects.findById(relatedObjectId)
								.map(this.converter::toBoundary);
						if (op.isEmpty()) {
							throw new InvalidInputException("Invalid relatedObjectId!");
						}
						updatedObject.setRelatedObjectId(relatedObjectId);
					}

				}
				details.remove("relatedObjectId"); // for optimal storage in db

				updatedObject.setObjectDetails(update.getObjectDetails());
			}

			this.logger.debug("Updated object: " + updatedObject.toString());
			return this.converter.toBoundary(this.objects.save(updatedObject));

		} else {
			this.logger.error("Object not found with object id - " + objectSystemID + "@@" + objectId);
			throw new ObjectNotFoundException("Couldn't find the object with object id - " + objectId);
		}
	}

	@Override
	@Deprecated
	public List<ObjectBoundary> getAll(String userSystemID, String userEmail) {
		this.logger.trace("getAll(" + userSystemID + ", " + userEmail + ")");
		this.logger.error("Deprecated operation - use getAll that uses pagination");
		throw new RuntimeException("Deprecated operation - use getAll that uses pagination");
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ObjectBoundary> getSpecificObject(String userSystemID, String userEmail, String objectSystemID,
			String objectId) {
		this.logger.trace("getSpecificObject(" + userSystemID + ", " + userEmail + ", " + objectSystemID + ", " + objectId + ")");

		// check if any of the ID's are either empty or only whitespace
		if (objectSystemID == null || objectId == null || objectSystemID.isBlank() || objectId.isBlank()) {
			this.logger.error("objectSystemID and objectId can't be blank");
			throw new InvalidInputException("objectSystemID and objectId can't be blank");
		}

		if (userSystemID == null || userEmail == null || userSystemID.isBlank() || userEmail.isBlank()) {
			this.logger.error("userSystemID and userEmail can't be blank");
			throw new InvalidInputException("userSystemID and userEmail can't be blank");
		}

		UserRole role = users.getUserRole(userSystemID, userEmail);
		String objectKey = objectSystemID + "@@" + objectId;

		this.logger.debug("UserRole: " + role.toString());
		this.logger.debug("ObjectKey: " + objectKey);

		switch (role) {
			case ADMIN:
				this.logger.error("Get a specific object by ID is unauthorized for admin users!");
				throw new UserUnauthorizedException("Get a specific object by ID is unauthorized for admin users!");
			case END_USER:
				this.logger.debug("Get a specific object by ID for end users");
				return this.objects.findByObjectIdAndActiveTrue(objectKey).map(this.converter::toBoundary);
			case OPERATOR:
				this.logger.debug("Get a specific object by ID for operators");
				return this.objects.findById(objectKey).map(this.converter::toBoundary);
			default:
				this.logger.error("Unexpected value: " + role);
				throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	@Override
	@Transactional
	public void deleteAllObjects(String adminSystemID, String adminEmail) {
		this.logger.trace("deleteAllObjects(" + adminSystemID + ", " + adminEmail + ")");

		if (users.getUserRole(adminSystemID, adminEmail) == UserRole.ADMIN) {
			this.objects.deleteAll();
			this.logger.info("All objects deleted successfully!");
			return;
		}

		this.logger.error("Only Admins are authorized to delete all objects!");
		throw new UserUnauthorizedException("Only Admins are authorized to delete all objects!");
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getAll(String userSystemID, String userEmail, int page, int size) {
		this.logger.trace("getAll(" + userSystemID + ", " + userEmail + ", " + page + ", " + size + ")");

		List<ObjectBoundary> rv = null;
		UserRole role = users.getUserRole(userSystemID, userEmail);

		this.logger.debug("UserRole: " + role.toString());

		if (role == UserRole.END_USER) {
			rv = this.objects
					.findAllByActiveTrue(PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
					.stream().map(this.converter::toBoundary).toList();
		} else if (role == UserRole.OPERATOR) {
			rv = this.objects.findAll(PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId")).stream()
					.map(this.converter::toBoundary).toList();
		} else {
			this.logger.error("User is not authorized to view all objects");
			throw new UserUnauthorizedException("User is not authorized to view all objects!");
		}
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByLocation(double lat, double lng, double distance, String distanceUnits,
			String userSystemID, String userEmail, int page, int size) {
		this.logger.trace("getObjectsByLocation(" + lat + ", " + lng + ", " + distance + ", " + distanceUnits + ", " + userSystemID + ", " + userEmail + ", " + page + ", " + size + ")");

		if (distance < 0.0) {
			this.logger.error("Distance must be positive!");
			throw new InvalidInputException("Distance must be positive!");
		}

		if (!distanceUnits.equals("NEUTRAL") && !distanceUnits.equals("MILES") && !distanceUnits.equals("KILOMETERS")) {
			this.logger.error("Distance Units must be one of NEUTRAL, KILOMETERS and MILES!");
			throw new InvalidInputException("Distance Units must be one of NEUTRAL, KILOMETERS and MILES!");
		}

		UserRole role = users.getUserRole(userSystemID, userEmail);

		this.logger.debug("UserRole: " + role.toString());

		switch (role) {
			case UserRole.END_USER:
				// find only active objects
				// already sorted by query
				this.logger.debug("End user - find only active objects");
				return this.objects
						.findAllWithinRadiusAndActiveIsTrue(lat, lng, distance, distanceUnits,
								PageRequest.of(page, size))
						.stream()
						.map(this.converter::toBoundary)
						.toList();

			case UserRole.OPERATOR:
				// find all objects
				// already sorted by query
				this.logger.debug("Operator - find all objects including inactive");
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
	public List<ObjectBoundary> getObjectsByType(String type, String userSystemID, String userEmail, int size,
			int page) {

		this.logger.trace("getObjectsByType(" + type + ", " + userSystemID + ", " + userEmail + ", " + size + ", " + page + ")");

		UserRole role = users.getUserRole(userSystemID, userEmail);

		this.logger.debug("UserRole: " + role.toString());

		switch (role) {
			case ADMIN:
				this.logger.error("Searching an object by type is unauthorized for admin users!");
				throw new UserUnauthorizedException("Searching an object by type is unauthorized for admin users!");
			case END_USER:
				this.logger.debug("End user - find only active objects");
				return this.objects
						.findAllByTypeAndActiveTrue(type,
								PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
						.stream().map(this.converter::toBoundary).toList();
			case OPERATOR:
				this.logger.debug("Operator - find all objects including inactive");
				return this.objects
						.findAllByType(type, PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
						.stream().map(this.converter::toBoundary).toList();
			default:
				this.logger.error("Unexpected value: " + role);
				throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByTypeAndStatus(String type, String status, String userSystemID,
			String userEmail, int size, int page) {
		this.logger.trace("getObjectsByTypeAndStatus(" + type + ", " + status + ", " + userSystemID + ", " + userEmail + ", " + size + ", " + page + ")");

		UserRole role = users.getUserRole(userSystemID, userEmail);

		this.logger.debug("UserRole: " + role.toString());

		switch (role) {
			case ADMIN:
				this.logger.error("Searching an object by type and status is unauthorized for admin users!");
				throw new UserUnauthorizedException(
						"Searching an object by type and status is unauthorized for admin users!");
			case END_USER:
				this.logger.debug("End user - find only active objects");
				return this.objects
						.findAllByTypeAndStatusAndActiveTrue(type, status,
								PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
						.stream().map(this.converter::toBoundary).toList();
			case OPERATOR:
				this.logger.debug("Operator - find all objects including inactive");
				return this.objects
						.findAllByTypeAndStatus(type, status,
								PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
						.stream().map(this.converter::toBoundary).toList();
			default:
				this.logger.error("Unexpected value: " + role);
				throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByAlias(String alias, String userSystemID, String userEmail, int size,
			int page) {
		this.logger.trace("getObjectsByAlias(" + alias + ", " + userSystemID + ", " + userEmail + ", " + size + ", " + page + ")");
		UserRole role = users.getUserRole(userSystemID, userEmail);

		this.logger.debug("UserRole: " + role.toString());

		switch (role) {
			case ADMIN:
				this.logger.error("Searching an object by alias is unauthorized for admin users!");
				throw new UserUnauthorizedException("Searching an object by alias is unauthorized for admin users!");

			case END_USER:
				this.logger.debug("End user - find only active objects");
				return this.objects
						.findAllByAliasAndActiveTrue(alias,
								PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
						.stream().map(this.converter::toBoundary).toList();

			case OPERATOR:
				this.logger.debug("Operator - find all objects including inactive");
				return this.objects
						.findAllByAlias(alias,
								PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
						.stream().map(this.converter::toBoundary).toList();
			default:
				this.logger.error("Unexpected value: " + role);
				throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getObjectsByAliasPattern(String pattern, String userSystemID, String userEmail,
			int size, int page) {

		this.logger.trace("getObjectsByAliasPattern(" + pattern + ", " + userSystemID + ", " + userEmail + ", " + size + ", " + page + ")");

		UserRole role = users.getUserRole(userSystemID, userEmail);

		this.logger.debug("UserRole: " + role.toString());

		switch (role) {
			case ADMIN:
				this.logger.error("Searching an object by alias pattern is unauthorized for admin users!");
				throw new UserUnauthorizedException(
						"Searching an object by alias pattern is unauthorized for admin users!");

			case END_USER:
				this.logger.debug("End user - find only active objects");
				return this.objects
						.findAllByAliasLikeAndActiveTrue("%" + pattern + "%",
								PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
						.stream().map(this.converter::toBoundary).toList();

			case OPERATOR:
				this.logger.debug("Operator - find all objects including inactive");
				return this.objects
						.findAllByAliasLike("%" + pattern + "%",
								PageRequest.of(page, size, Direction.DESC, "creationTime", "objectId"))
						.stream().map(this.converter::toBoundary).toList();
			default:
				this.logger.error("Unexpected value: " + role);
				throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ObjectBoundary> getPlantsForWatering(String userSystemID, String userEmail, int size, int page) {
		UserRole role = users.getUserRole(userSystemID, userEmail);
		switch (role) {
			case ADMIN:
				throw new UserUnauthorizedException("Searching plants for watering is unauthorized for admin users!");
			case END_USER:
				return this.objects.findAllByTypeIsPlantAndActiveIsTrueAndNeedWatering(
					     PageRequest.of(page, size))
				.stream().map(this.converter::toBoundary).toList();

			case OPERATOR:
				return this.objects.findAllByTypeIsPlantAndNeedWatering(
					     PageRequest.of(page, size))
				.stream().map(this.converter::toBoundary).toList();
			default:
				throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

}
