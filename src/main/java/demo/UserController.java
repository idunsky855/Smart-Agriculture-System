package demo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = {"/aii/users"})
public class UserController {
	private Map<String,UserBoundary> usersDatabase = new ConcurrentHashMap<>();
	
	@GetMapping(
		path = {"/login/{systemID}/{userEmail}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public UserBoundary validateLoginAndGetUserDetails(
			@PathVariable("systemID") String systemID, @PathVariable("userEmail") String userEmail) {
		
		usersDatabase.put("avital.shmueli@s.afeka.ac.il", new UserBoundary("avital.shmueli@s.afeka.ac.il","user","avital",""));
		usersDatabase.put("hadar.zimberg.tau@s.afeka.ac.il", new UserBoundary("hadar.zimberg.tau@s.afeka.ac.il","user","hadar",""));
		
		UserBoundary rv = new UserBoundary();
		if (userEmail == null || userEmail.trim().isEmpty()) {
			userEmail = "Anonymous";
		}
		
		rv = usersDatabase.get(userEmail);
		
		System.err.println("*** " + rv);
		
		return rv;
	}

}
