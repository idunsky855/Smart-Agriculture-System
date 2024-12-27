package aii.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = -2735731730567841539L;

	public UserUnauthorizedException() {
	}

	public UserUnauthorizedException(String user) {
		super(user);
	}

	public UserUnauthorizedException(Throwable cause) {
		super(cause);
	}

	public UserUnauthorizedException(String user, Throwable cause) {
		super(user, cause);
	}

}
