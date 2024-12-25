package aii.logic.converters;

import org.springframework.stereotype.Component;

import aii.data.UserEntity;
import aii.logic.UserBoundary;
import aii.logic.UserId;

@Component
public class UserConverter {

	public UserEntity toEntity(UserBoundary boundary) {

		UserEntity entity = new UserEntity();

		entity.setUserId(boundary.getUserId());
		entity.setRole(boundary.getRole());
		entity.setUsername(boundary.getUsername());
		entity.setAvatar(boundary.getAvatar());

		return entity;
	}

	public UserBoundary toBoundary(UserEntity entity) {

		UserBoundary boundary = new UserBoundary();

		String systemID = entity.getUserId().split("@@")[0];
		String email = entity.getUserId().split("@@")[1];
		boundary.setUserId(new UserId(systemID, email));
		boundary.setRole(entity.getRole());
		boundary.setUsername(entity.getUsername());
		boundary.setAvatar(entity.getAvatar());

		return boundary;
	}

}
