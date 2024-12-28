package aii.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import aii.data.CommandEntity;

public interface CommandsCrud extends JpaRepository<CommandEntity, String>{
}
