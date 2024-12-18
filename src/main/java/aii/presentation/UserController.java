package aii.presentation;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aii.logic.InvalidInputException;
import aii.logic.NewUserBoundary;
import aii.logic.UserBoundary;
import aii.logic.UserId;
import aii.logic.UserNotFoundException;
import aii.logic.UsersService;

@RestController
@RequestMapping(path = { "/aii" })
public class UserController {

	private UsersService users;

	//private String springApplicationName;
	private Map<String, UserBoundary> usersDB;

	public UserController(UsersService users) {
		this.users = users;
		// this.usersDB = new ConcurrentHashMap<>();
	}

	/*@Value("${spring.application.name:defaultAppName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
		System.err.println("********" + this.springApplicationName);
	}*/

	@GetMapping(path = { "/users/login/{systemID}/{userEmail}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary validateLoginAndGetUserDetails(@PathVariable("systemID") String systemID,
			@PathVariable("userEmail") String userEmail) {
/*
		if (systemID == null || systemID.trim().isEmpty())
			throw new RuntimeException("Invalid input - systemID is not initialized");

		if (userEmail == null || userEmail.trim().isEmpty())
			throw new RuntimeException("Invalid input - user email is not initialized");

		UserBoundary rv = new UserBoundary();
		String key = systemID + "@@" + userEmail;
		rv = usersDB.get(key);

		if (rv == null)
			throw new RuntimeException("Could not find user by id: " + key);

		System.err.println("*** " + rv);

		return rv;*/
		return this.users
				.login(systemID, userEmail)
				.orElseThrow(() -> new UserNotFoundException(
						"Could not find user by id: " + systemID + "@@" + userEmail));
		
	}

	@PostMapping(path = { "/users" }, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public UserBoundary insertToDb(@RequestBody NewUserBoundary newUser) {

		if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty())
			throw new InvalidInputException("Invalid input - email is not initialized");

		if (newUser.getRole() == null)
			throw new InvalidInputException("Invalid input - role is not initialized");

		/*if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty())
			throw new RuntimeException("Invalid input - username is not initialized");

		if (newUser.getAvatar() != null && newUser.getAvatar().length() > 0 && newUser.getAvatar().trim().isEmpty())
			throw new RuntimeException("Invalid input - avatar cannot be all blank spaces");
*/
		UserBoundary rv = new UserBoundary();
		rv.setUsername(newUser.getUsername());
		rv.setRole(newUser.getRole());
		rv.setAvatar(newUser.getAvatar());
		UserId userId = new UserId();
		userId.setEmail(newUser.getEmail());
		rv.setUserId(userId);
		//rv.setUserId(new UserId(springApplicationName, newUser.getEmail()));

		/*String key = springApplicationName + "@@" + rv.getUserId().getEmail();
		if (this.usersDB.containsKey(key))
			throw new RuntimeException("A user with the same email is already exist in the system.");

		this.usersDB.put(key, rv);
		return rv;*/
		
		return this.users
				.createUser(rv);
	}

	@PutMapping(path = { "/users/{systemID}/{userEmail}" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public void updateUser(@PathVariable("systemID") String systemID, @PathVariable("userEmail") String userEmail,
			@RequestBody UserBoundary update) {

		/*if (systemID == null || systemID.trim().isEmpty())
			throw new RuntimeException("Invalid input - systemID is not initialized");

		if (userEmail == null || userEmail.trim().isEmpty())
			throw new RuntimeException("Invalid input - user email is not initialized");

		String key = systemID + "@@" + userEmail;
		if (this.usersDB.containsKey(key)) {
			// update usersDB
			UserBoundary updatedUser = this.usersDB.get(key);
			boolean dirty = false;

			if (update.getRole() != null) {
				updatedUser.setRole(update.getRole());
				dirty = true;
			}

			if (update.getUsername() != null) {
				if (update.getUsername().trim().isEmpty())
					throw new RuntimeException("Invalid input - username cannot be all blank spaces");

				updatedUser.setUsername(update.getUsername());
				dirty = true;
			}

			if (update.getAvatar() != null) {
				if (update.getAvatar().length() > 0 && update.getAvatar().trim().isEmpty())
					throw new RuntimeException("Invalid input - avatar cannot be all blank spaces");

				updatedUser.setAvatar(update.getAvatar());
				dirty = true;
			}

			if (dirty) {
				this.usersDB.put(key, updatedUser);
			}
		} else {
			throw new RuntimeException("Could not find user by id: " + key);
		}*/
		
		this.users
		.updateUser(systemID, userEmail, update);
	}

	@GetMapping(path = { "/admin/users" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary[] exportAllUsers() {
		//return usersDB.values().toArray(new UserBoundary[0]);
		
		return this.users
				.getAllUsers()
				.toArray(new UserBoundary[0]);
	}

	@DeleteMapping(path = { "/admin/users" })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllUsers() {
		//this.usersDB.clear();
		this.users
		.deleteAllUsers();
	}

}
