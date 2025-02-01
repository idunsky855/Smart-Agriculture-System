package aii.logic;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aii.dal.UsersCrud;
import aii.data.UserEntity;
import aii.data.UserRole;
import aii.logic.converters.UserConverter;
import aii.logic.exceptions.InvalidInputException;
import aii.logic.exceptions.UserAlreadyExistsException;
import aii.logic.exceptions.UserNotFoundException;
import aii.logic.exceptions.UserUnauthorizedException;
import aii.logic.utilities.EmailValidator;
import jakarta.annotation.PostConstruct;

@Service
public class UsersServiceImplementation implements EnhancedUsersService {

	private UsersCrud users;
	private UserConverter converter;
	private String springApplicationName;
	private EmailValidator emailValidator;
	private Log logger = LogFactory.getLog(UsersServiceImplementation.class);

	public UsersServiceImplementation(UsersCrud users, UserConverter converter) {
		this.users = users;
		this.converter = converter;
		emailValidator = new EmailValidator();
	}

	@Value("${spring.application.name:defaultAppName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.logger.trace("setSpringApplicationName(" + springApplicationName + ")");

		this.springApplicationName = springApplicationName;

		this.logger.debug(this.springApplicationName);}

	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {

		if (user == null || user.getUserId() == null)
			throw new InvalidInputException("Invalid input - user is not initialized");

		//Log:
		this.logger.trace("createUser(" + user.toString() + ")");

		if (!emailValidator.isEmailValid(user.getUserId().getEmail())) {
			this.logger.error("Invalid email: " + user.getUserId().getEmail());
			throw new InvalidInputException("Invalid input - invalid email");
		}

		if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
			this.logger.error("Invalid username: " + user.getUsername());
			throw new InvalidInputException("Invalid input - username is not initialized");
		}

		if (user.getAvatar() == null || user.getAvatar().trim().isEmpty()) {
			this.logger.error("Invalid avatar: " + user.getAvatar());
			throw new InvalidInputException("Invalid input - avatar cannot be all blank spaces");
		}

		user.setUserId(new UserId(springApplicationName, user.getUserId().getEmail()));

		this.logger.debug("User ID: " + user.getUserId().getSystemID() + "@@" + user.getUserId().getEmail() + " has been set");

		if (!login(user.getUserId().getSystemID(),user.getUserId().getEmail()).isEmpty()) {
			this.logger.error("User already exists: " + user.getUserId().getEmail());
			throw new UserAlreadyExistsException("A user with the same email already exists in the system");
		}

		return this.converter.toBoundary(this.users.save(this.converter.toEntity(user)));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserBoundary> login(String systemID, String userEmail) {
		this.logger.trace("login(" + systemID + ", " + userEmail + ")");

		if (systemID == null || systemID.trim().isEmpty()) {
			this.logger.error("Invalid systemID: " + systemID);
			throw new InvalidInputException("Invalid input - systemID is not initialized");
		}

		if (userEmail == null || userEmail.trim().isEmpty()) {
			this.logger.error("Invalid email: " + userEmail);
			throw new InvalidInputException("Invalid input - user email is not initialized");
		}

		if(!emailValidator.isEmailValid(userEmail)) {
			this.logger.error("Invalid email: " + userEmail);
			throw new InvalidInputException("Invalid input - invalid email");
		}

		return this.users.findById(systemID + "@@" + userEmail).map(this.converter::toBoundary);
	}

	@Override
	@Transactional
	public UserBoundary updateUser(String systemID, String userEmail, UserBoundary update) {

		if (update == null) {
			this.logger.error("Invalid update - update is null");
			throw new InvalidInputException("Invalid input - update is not initialized");
		}

		this.logger.trace("updateUser(" + systemID + ", " + userEmail + ", " + update.toString() + ")");

		if (systemID == null || systemID.trim().isEmpty()) {
			this.logger.error("Invalid systemID - systemID is null or empty");
			throw new InvalidInputException("Invalid input - systemID is not initialized");
		}

		if (userEmail == null || userEmail.trim().isEmpty()) {
			this.logger.error("Invalid email - userEmail is null or empty");
			throw new InvalidInputException("Invalid input - user email is not initialized");
		}

		if(!emailValidator.isEmailValid(userEmail)) {
			this.logger.error("Invalid email: " + userEmail);
			throw new InvalidInputException("Invalid input - invalid email");
		}

		String key = systemID + "@@" + userEmail;
		Optional<UserEntity> entityOp = this.users
				.findById(key);

		if(!entityOp.isEmpty()) {
			this.logger.debug("User found: " + key);
			UserEntity updatedUser = entityOp.get();

			if (update.getRole() != null)
				updatedUser.setRole(update.getRole());

			if (update.getUsername() != null) {
				if (update.getUsername().trim().isEmpty()) {
					this.logger.error("Invalid username - username is all blank spaces");
					throw new InvalidInputException("Invalid input - username cannot be all blank spaces");
				}

				updatedUser.setUsername(update.getUsername());
			}

			if (update.getAvatar() != null) {
				if (update.getAvatar().trim().isEmpty()) {
					this.logger.error("Invalid avatar - avatar is all blank spaces");
					throw new InvalidInputException("Invalid input - avatar cannot be all blank spaces");
				}

				updatedUser.setAvatar(update.getAvatar());
			}

			return this.converter.toBoundary(this.users.save(updatedUser));

		} else {
			this.logger.error("User not found: " + key);
			throw new UserNotFoundException("Could not find user by id: " + key);
		}

	}

	@Override
	@Deprecated
	public List<UserBoundary> getAllUsers(String adminSystemID, String adminEmail) {
		this.logger.trace("getAllUsers(" + adminSystemID + ", " + adminEmail + ")");
		this.logger.error("Deprecated operation - use getAllUsers that uses pagination");
		throw new RuntimeException("Deprecated operation - use getAllUsers that uses pagination");
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(String adminSystemID, String adminEmail, int size, int page) {
		this.logger.trace("getAllUsers(" + adminSystemID + ", " + adminEmail + ", " + size + ", " + page + ")");

		UserRole role = getUserRole(adminSystemID, adminEmail);

		this.logger.debug("UserRole: " + role.toString());

		switch (role) {
		case ADMIN:
			this.logger.debug("Returning all users");
			return this.users.findAll(PageRequest.of(page, size, Direction.DESC, "username", "userId")).stream().map(this.converter::toBoundary).toList();
		case END_USER:
			this.logger.error("User is unauthorized to view all users: email: " + adminEmail + " role: " + role);
			throw new UserUnauthorizedException(
					"User is unauthorized to view all users: email: " + adminEmail + " role: " + role);

		case OPERATOR:
			this.logger.error("User is unauthorized to view all users: email: " + adminEmail + " role: " + role);
			throw new UserUnauthorizedException(
					"User is unauthorized to view all users!");

		default:
			this.logger.error("Unexpected value: " + role);
			throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	@Override
	@Transactional
	public void deleteAllUsers(String adminSystemID, String adminEmail) {
		this.logger.trace("deleteAllUsers(" + adminSystemID + ", " + adminEmail + ")");

		UserRole role = getUserRole(adminSystemID, adminEmail);

		this.logger.debug("UserRole: " + role.toString());

		switch (role) {
		case ADMIN:
			this.users.deleteAll();
			if (this.users.findAll().size() != 0)
				throw new RuntimeException("Error while deleting users list");
			break;
		case END_USER:
			this.logger.error("User is unauthorized to delete all users: email: " + adminEmail + " role: " + role);
			throw new UserUnauthorizedException(
					"User is unauthorized to delete all users!");

		case OPERATOR:
			this.logger.error("User is unauthorized to delete all users: email: " + adminEmail + " role: " + role);
			throw new UserUnauthorizedException(
					"User is unauthorized to delete all users!");

		default:
			this.logger.error("Unexpected value: " + role);
			throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	public UserRole getUserRole(String systemID, String email) {
		this.logger.trace("getUserRole(" + systemID + ", " + email + ")");

	    if (systemID == null || systemID.trim().isEmpty()) {
			this.logger.error("Invalid systemID: " + systemID);
	        throw new InvalidInputException("Invalid input - systemID is not initialized");
		}

	    if (email == null || email.trim().isEmpty()) {
			this.logger.error("Invalid email: " + email);
	        throw new InvalidInputException("Invalid input - email is not initialized");
		}

	    if (!emailValidator.isEmailValid(email)) {
			this.logger.error("Invalid email: " + email);
	        throw new InvalidInputException("Invalid input - invalid email");
		}

	    String key = systemID + "@@" + email;

	    Optional<UserEntity> entityOp = this.users.findById(key);

	    if (entityOp.isPresent()) {
			this.logger.debug("User found: " + key);
	        return entityOp.get().getRole(); // Return the actual role of the user
		} else {
			this.logger.error("User not found: " + key);
	        throw new UserUnauthorizedException("User is not authorized to perform this operation");
		}
	}

	@PostConstruct
	private void init() {
		this.logger.trace("init()");
		CompletableFuture.runAsync(this::initializeDefaultUsers);
	}

	@Transactional
	private void initializeDefaultUsers() {
		this.logger.trace("initializeDefaultUsers()");

		this.logger.debug("Creating default sensor user");

		UserBoundary defaultSensorUser = new UserBoundary();

		// Create user with email: sensor@default.com and id: 2025a.Liron.Barshishat:
		defaultSensorUser.setUserId(new UserId("2025a.Liron.Barshishat", "sensor@default.com"));
		defaultSensorUser.setUsername("Sensor");
		defaultSensorUser.setRole(UserRole.OPERATOR);
		defaultSensorUser.setAvatar("sensor");

		// Verify that the user does not exist in the database when DB is not empty on startup:
		if (!login(defaultSensorUser.getUserId().getSystemID(), defaultSensorUser.getUserId().getEmail()).isEmpty()) {
			this.logger.debug("User already exists when creating default user: " + defaultSensorUser.getUserId().getEmail());
		} else {
			// Call createUser to insert the default user to the database:
			createUser(defaultSensorUser);
		}

		this.logger.debug("Default sensor user created");
		this.logger.debug("Creating default irrigation user");

		UserBoundary defaultIrrigationUser = new UserBoundary();

		// Create user with email: irrigation@default.com and id: 2025a.Liron.Barshishat:
		defaultIrrigationUser.setUserId(new UserId("2025a.Liron.Barshishat", "irrigation@default.com"));
		defaultIrrigationUser.setUsername("Irrigation");
		defaultIrrigationUser.setRole(UserRole.END_USER);
		defaultIrrigationUser.setAvatar("irrigation");

		// Verify that the user does not exist in the database when DB is not empty on startup:
		if (!login(defaultIrrigationUser.getUserId().getSystemID(), defaultIrrigationUser.getUserId().getEmail()).isEmpty()) {
			this.logger.debug("User already exists when creating default user: " + defaultIrrigationUser.getUserId().getEmail());
		} else {
			// Call createUser to insert the default user to the database:
			createUser(defaultIrrigationUser);
		}

		this.logger.debug("Default irrigation user created");
	}
}
