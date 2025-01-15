package aii.logic;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
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

import java.util.List;


import org.springframework.stereotype.Service;

@Service
public class UsersServiceImplementation implements EnhancedUsersService {

	private UsersCrud users;
	private UserConverter converter;
	private String springApplicationName;
	private EmailValidator emailValidator;
	
	public UsersServiceImplementation(UsersCrud users, UserConverter converter) {
		this.users = users;
		this.converter = converter;
		emailValidator = new EmailValidator();
	}

	@Value("${spring.application.name:defaultAppName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
		System.out.println("[DEBUG] " + this.springApplicationName);
	}

	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {
		
		if (user == null || user.getUserId() == null)
			throw new InvalidInputException("Invalid input - user is not initialized");
		
		if (!emailValidator.isEmailValid(user.getUserId().getEmail()))
			throw new InvalidInputException("Invalid input - invalid email");
		
		if (user.getUsername() == null || user.getUsername().trim().isEmpty())
			throw new InvalidInputException("Invalid input - username is not initialized");

		if (user.getAvatar() == null || user.getAvatar().trim().isEmpty())
			throw new InvalidInputException("Invalid input - avatar cannot be all blank spaces");

		user.setUserId(new UserId(springApplicationName, user.getUserId().getEmail()));
		
		if (!login(user.getUserId().getSystemID(),user.getUserId().getEmail()).isEmpty())
			throw new UserAlreadyExistsException("A user with the same email is already exists in the system");

		return this.converter.toBoundary(this.users.save(this.converter.toEntity(user)));
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserBoundary> login(String systemID, String userEmail) {
		
		if (systemID == null || systemID.trim().isEmpty())
			throw new InvalidInputException("Invalid input - systemID is not initialized");

		if (userEmail == null || userEmail.trim().isEmpty())
			throw new InvalidInputException("Invalid input - user email is not initialized");
		
		if(!emailValidator.isEmailValid(userEmail))
			throw new InvalidInputException("Invalid input - invalid email");
		
		return this.users.findById(systemID + "@@" + userEmail).map(this.converter::toBoundary);
	}

	@Override
	@Transactional
	public UserBoundary updateUser(String systemID, String userEmail, UserBoundary update) {
		
		if (systemID == null || systemID.trim().isEmpty())
			throw new InvalidInputException("Invalid input - systemID is not initialized");

		if (userEmail == null || userEmail.trim().isEmpty())
			throw new InvalidInputException("Invalid input - user email is not initialized");
		
		if(!emailValidator.isEmailValid(userEmail))
			throw new InvalidInputException("Invalid input - invalid email");
		
		String key = systemID + "@@" + userEmail;
		Optional<UserEntity> entityOp = this.users
				.findById(key);

		if(!entityOp.isEmpty()) {
			UserEntity updatedUser = entityOp.get();

			if (update.getRole() != null)
				updatedUser.setRole(update.getRole());

			if (update.getUsername() != null) {
				if (update.getUsername().trim().isEmpty())
					throw new InvalidInputException("Invalid input - username cannot be all blank spaces");
				updatedUser.setUsername(update.getUsername());
			}

			if (update.getAvatar() != null) {
				if (update.getAvatar().trim().isEmpty())
					throw new InvalidInputException("Invalid input - avatar cannot be all blank spaces");
				updatedUser.setAvatar(update.getAvatar());
			}
			
			return this.converter.toBoundary(this.users.save(updatedUser));

		} else {
			throw new UserNotFoundException("Could not find user by id: " + key);
		}
		
	}

	@Override
	@Deprecated
	public List<UserBoundary> getAllUsers(String adminSystemID, String adminEmail) {
		//return this.users.findAll().stream().map(this.converter::toBoundary).toList();
		throw new RuntimeException("Deprecated operation - use getAllUsers that uses pagination");
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(String adminSystemID, String adminEmail, int size, int page) {
		
		try {
			UserRole role = getUserRole(adminSystemID, adminEmail);

			switch (role) {
			case ADMIN:
				return this.users.findAll(PageRequest.of(page, size, Direction.DESC, "username", "role")).stream().map(this.converter::toBoundary).toList();
			case END_USER:
				throw new UserUnauthorizedException(
						"Exporting all users is unauthorized for end users!");

			case OPERATOR:
				throw new UserUnauthorizedException(
						"Exporting all users is unauthorized for operators!");

			default:
				throw new IllegalArgumentException("Unexpected value: " + role);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional
	public void deleteAllUsers(String adminSystemID, String adminEmail) {

		try {
			UserRole role = getUserRole(adminSystemID, adminEmail);

			switch (role) {
			case ADMIN:
				this.users.deleteAll();
				if (this.users.findAll().size() != 0)
					throw new RuntimeException("Error while deleting users list");
				break;
			case END_USER:
				throw new UserUnauthorizedException(
						"Deleting a user is unauthorized for end users!");

			case OPERATOR:
				throw new UserUnauthorizedException(
						"Deleting a user is unauthorized for operators!");

			default:
				throw new IllegalArgumentException("Unexpected value: " + role);
			}

		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional(readOnly = true)
	public UserRole getUserRole(String systemID, String email) {
		
	    if (systemID == null || systemID.trim().isEmpty())
	        throw new InvalidInputException("Invalid input - systemID is not initialized");

	    if (email == null || email.trim().isEmpty())
	        throw new InvalidInputException("Invalid input - email is not initialized");

	    if (!emailValidator.isEmailValid(email))
	        throw new InvalidInputException("Invalid input - invalid email");

	    String key = systemID + "@@" + email;

	    Optional<UserEntity> entityOp = this.users.findById(key);

	    if (entityOp.isPresent())
	        return entityOp.get().getRole(); // Return the actual role of the user
	    else
	        throw new UserUnauthorizedException("The operation is not authorized for user with id: " + key);
	}
}
