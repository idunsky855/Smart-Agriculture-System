package aii.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -2509731730564940439L;

	public UserNotFoundException() {
	}

	public UserNotFoundException(String user) {
		super(user);
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

	public UserNotFoundException(String user, Throwable cause) {
		super(user, cause);
	}

}
