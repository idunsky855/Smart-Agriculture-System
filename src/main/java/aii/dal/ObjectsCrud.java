package aii.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import aii.data.ObjectEntity;

public interface ObjectsCrud extends JpaRepository<ObjectEntity, String>{
}