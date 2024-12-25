package aii.logic;

import java.util.List;
import java.util.Optional;

public interface UsersService {
	
	public UserBoundary createUser(UserBoundary user);
	
	public Optional<UserBoundary> login(String systemID, String userEmail);
	
	public UserBoundary updateUser(String systemID, String userEmail, UserBoundary update);
	
	public List<UserBoundary> getAllUsers(String adminSystemID, String adminEmail);
	
	public void deleteAllUsers(String adminSystemID, String adminEmail);
}
