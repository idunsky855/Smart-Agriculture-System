package aii.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidCommandException extends RuntimeException {
	private static final long serialVersionUID = -1589031133148186129L;

	public InvalidCommandException() {
	}

	public InvalidCommandException(String message) {
		super(message);
	}

	public InvalidCommandException(Throwable cause) {
		super(cause);
	}

	public InvalidCommandException(String message, Throwable cause) {
		super(message, cause);
	}
}
