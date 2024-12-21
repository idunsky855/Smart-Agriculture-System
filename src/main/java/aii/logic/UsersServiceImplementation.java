package aii.logic;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import aii.dal.UsersCrud;
import aii.data.UserEntity;
import aii.logic.EmailValidator;

import java.util.List;


import org.springframework.stereotype.Service;

@Service
public class UsersServiceImplementation implements UsersService {

	private UsersCrud users;
	private String springApplicationName;
	private EmailValidator emailValidator;
	

	public UsersServiceImplementation(UsersCrud users) {
		this.users = users;
		emailValidator = new EmailValidator();
	}

	@Value("${spring.application.name:defaultAppName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
		System.err.println("********" + this.springApplicationName);
	}

	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {
		
		if (user == null || user.getUserId() == null)
			throw new InvalidInputException("Invalid input - user is not initialized");
		
		if (user.getUserId().getEmail() == null || user.getUserId().getEmail().trim().isEmpty())
			throw new InvalidInputException("Invalid input - email is not initialized");

		if (user.getRole() == null)
			throw new InvalidInputException("Invalid input - role is not initialized");
	
		if (!emailValidator.isEmailValid(user.getUserId().getEmail()))
			throw new InvalidInputException("Invalid input - invalid email");
		
		if (user.getUsername() == null || user.getUsername().trim().isEmpty())
			throw new InvalidInputException("Invalid input - username is not initialized");

		if (user.getAvatar() != null && user.getAvatar().length() > 0 && user.getAvatar().trim().isEmpty())
			throw new InvalidInputException("Invalid input - avatar cannot be all blank spaces");

		user.setUserId(new UserId(springApplicationName, user.getUserId().getEmail()));
		
		if (!login(user.getUserId().getSystemID(),user.getUserId().getEmail()).isEmpty())
			throw new UserAlreadyExistsException("A user with the same email is already exists in the system");

		return new UserBoundary(this.users.save(user.toEntity()));
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
		
		return this.users.findById(systemID + "@@" + userEmail).map(UserBoundary::new);
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
				if (update.getAvatar().length() > 0 && update.getAvatar().trim().isEmpty())
					throw new InvalidInputException("Invalid input - avatar cannot be all blank spaces");
				updatedUser.setAvatar(update.getAvatar());
			}
			
			return new UserBoundary(this.users.save(updatedUser));

		} else {
			throw new UserNotFoundException("Could not find user by id: " + key);
		}
		
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers() {
		return this.users.findAll().stream().map(UserBoundary::new).toList();
	}

	@Override
	@Transactional
	public void deleteAllUsers() {
		this.users.deleteAll();
		
		if (this.users.findAll().size() != 0)
			throw new RuntimeException("Error while deleting users list");
	}

}
