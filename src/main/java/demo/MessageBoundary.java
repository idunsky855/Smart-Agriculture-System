package demo;

import java.util.Date;

public class MessageBoundary {
	private String message;
	private Date createdTimestamp;

	public MessageBoundary() {
	}

	public MessageBoundary(String message) {
		this.message = message;
		this.createdTimestamp = new Date();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	@Override
	public String toString() {
		return "MessageBoundary [message=" + message + ", createdTimestamp=" + createdTimestamp + "]";
	}

}
