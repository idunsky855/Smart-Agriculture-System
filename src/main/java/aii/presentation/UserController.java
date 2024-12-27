package aii.presentation;

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

import aii.logic.NewUserBoundary;
import aii.logic.UserBoundary;
import aii.logic.UserId;
import aii.logic.UsersService;
import aii.logic.exceptions.InvalidInputException;
import aii.logic.exceptions.UserUnauthorizedException;

@RestController
@RequestMapping(path = { "/aii" })
public class UserController {

	private UsersService users;

	public UserController(UsersService users) {
		this.users = users;
	}

	@GetMapping(path = { "/users/login/{systemID}/{userEmail}" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary validateLoginAndGetUserDetails(@PathVariable("systemID") String systemID,
			@PathVariable("userEmail") String userEmail) {

		return this.users
				.login(systemID, userEmail)
				.orElseThrow(() -> new UserUnauthorizedException(
						"Could not find user by id or user is unauthorized: " + systemID + "@@" + userEmail));
	}

	@PostMapping(path = { "/users" }, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	public UserBoundary insertToDb(@RequestBody NewUserBoundary newUser) {

		if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty())
			throw new InvalidInputException("Invalid input - email is not initialized");

		if (newUser.getRole() == null)
			throw new InvalidInputException("Invalid input - role is not initialized");

		UserBoundary rv = new UserBoundary();
		rv.setUsername(newUser.getUsername());
		rv.setRole(newUser.getRole());
		rv.setAvatar(newUser.getAvatar());
		UserId userId = new UserId();
		userId.setEmail(newUser.getEmail());
		rv.setUserId(userId);

		return this.users
				.createUser(rv);
	}

	@PutMapping(path = { "/users/{systemID}/{userEmail}" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public void updateUser(@PathVariable("systemID") String systemID, @PathVariable("userEmail") String userEmail,
			@RequestBody UserBoundary update) {

		this.users
		.updateUser(systemID, userEmail, update);
	}

	@GetMapping(path = { "/admin/users" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public UserBoundary[] exportAllUsers() {

		return this.users
				.getAllUsers("","")
				.toArray(new UserBoundary[0]);
	}

	@DeleteMapping(path = { "/admin/users" })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllUsers() {

		this.users
		.deleteAllUsers("","");
	}

}
