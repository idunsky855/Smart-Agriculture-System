package aii.logic;

import java.util.List;

import aii.data.UserRole;

public interface EnhancedUsersService extends UsersService {
	
	public UserRole getUserRole(String systemID, String email);
	
	public List<UserBoundary> getAllUsers(String adminSystemID, String adminEmail, int size, int page);

}
