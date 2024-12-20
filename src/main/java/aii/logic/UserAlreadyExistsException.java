package aii.logic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException {
	
	private static final long serialVersionUID = -2509731730564948783L;

	public UserAlreadyExistsException() {
		
	}

	public UserAlreadyExistsException(String user) {
		super(user);
	}

	public UserAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public UserAlreadyExistsException(String user, Throwable cause) {
		super(user, cause);
	}

}
