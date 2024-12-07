package demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/aii/admins"})
public class AdminController {
	private Map<String,UserBoundary> usersDatabase = new ConcurrentHashMap<>();
	
	@GetMapping(
			path = {"/users"},
			produces = {MediaType.APPLICATION_JSON_VALUE})
		public UserBoundary[] exportAllUsers() {
		usersDatabase.put("avital.shmueli@s.afeka.ac.il", new UserBoundary("avital.shmueli@s.afeka.ac.il","user","avital",""));
		usersDatabase.put("hadar.zimberg.tau@s.afeka.ac.il", new UserBoundary("hadar.zimberg.tau@s.afeka.ac.il","user","hadar",""));
		
		return usersDatabase.values().toArray(new UserBoundary[0]);
	}

}
