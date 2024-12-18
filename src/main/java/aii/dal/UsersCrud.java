package aii.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import aii.data.UserEntity;

public interface UsersCrud extends JpaRepository<UserEntity, String>{
	

}
