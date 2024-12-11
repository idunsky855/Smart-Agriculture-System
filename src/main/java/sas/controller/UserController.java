package sas.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sas.boundary.NewUserBoundary;
import sas.boundary.UserBoundary;
import sas.boundary.UserId;

@RestController
@RequestMapping(path = { "/aii" })
public class UserController {
	@Value("${spring.application.name:defaultAppName}")
	private String springApplicationName;
	private Map<String, UserBoundary> usersDB;

	public UserController() {
		this.usersDB = new ConcurrentHashMap<>();
	}


	@GetMapping(path = { "/users/login/{systemID}/{userEmail}" }, 
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary validateLoginAndGetUserDetails(@PathVariable("systemID") String systemID,
			@PathVariable("userEmail") String userEmail) {

		if (systemID == null || systemID.trim().isEmpty() || userEmail == null || userEmail.trim().isEmpty())
			throw new RuntimeException("Invalid input - either systemID or user email are not initialized");

		UserBoundary rv = new UserBoundary();
		String key = systemID + "@@" + userEmail;
		rv = usersDB.get(key);
		
		if(rv == null)
			throw new RuntimeException("Could not find user by id: " + key);
		
		System.err.println("*** " + rv);

		return rv;
	}

	
	@PostMapping(path = { "/users" }, 
			consumes = { MediaType.APPLICATION_JSON_VALUE }, 
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary insertToDb(@RequestBody NewUserBoundary newUser) {

		if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty() || newUser.getRole() == null)
			throw new RuntimeException("Invalid input - either email or role are not initialized");

		UserBoundary rv = new UserBoundary();
		rv.setUsername(newUser.getUsername());
		rv.setRole(newUser.getRole());
		rv.setAvatar(newUser.getAvatar());
		rv.setUserId(new UserId(springApplicationName, newUser.getEmail()));

		this.usersDB.put(springApplicationName + "@@" + rv.getUserId().getEmail(), rv);
		return rv;
	}

	
	@PutMapping(path = { "/users/{systemID}/{userEmail}" }, 
			consumes = { MediaType.APPLICATION_JSON_VALUE })
	public void updateUser(@PathVariable("systemID") String systemID, 
			@PathVariable("userEmail") String userEmail,
			@RequestBody UserBoundary update) {
		
		if (systemID == null || systemID.trim().isEmpty() || userEmail == null || userEmail.trim().isEmpty())
			throw new RuntimeException("Invalid input - either systemID or user email are not initialized");
		
		String key = systemID + "@@" + userEmail;
		if (this.usersDB.containsKey(key)) {
			// update usersDB
			UserBoundary updatedUser = this.usersDB.get(key);
			boolean dirty = false;

			if (update.getUserId() != null) {
				// do nothing
			}

			if (update.getRole() != null) {
				updatedUser.setRole(update.getRole());
				dirty = true;
			}

			if (update.getUsername() != null) {
				updatedUser.setUsername(update.getUsername());
				dirty = true;
			}

			if (update.getAvatar() != null) {
				updatedUser.setAvatar(update.getAvatar());
				dirty = true;
			}

			if (dirty) {
				this.usersDB.put(key, updatedUser);
			}
		} else {
			throw new RuntimeException("Could not find user by id: " + key);
		}
	}
	
	@GetMapping(path = { "/admin/users" }, 
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary[] exportAllUsers() {
		return usersDB.values().toArray(new UserBoundary[0]);
	}

	
	@DeleteMapping(path = { "/admin/users"})
	public void deleteAllUsers() {
		this.usersDB.clear();
	}

}
