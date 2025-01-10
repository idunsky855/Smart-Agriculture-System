package aii.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import aii.data.ObjectEntity;

public interface ObjectsCrud extends JpaRepository<ObjectEntity, String> {

	public List<ObjectEntity> findAllByTypeIgnoreCaseAndActiveTrue(@Param("type") String type, Pageable pageable);

	public List<ObjectEntity> findAllByTypeAndStatusIgnoreCaseAndActiveTrue(@Param("type") String type,
			@Param("status") String status, Pageable pageable);

	public List<ObjectEntity> findAllByTypeIgnoreCase(@Param("type") String type, Pageable pageable);

	public List<ObjectEntity> findAllByTypeAndStatusIgnoreCase(@Param("type") String type,
			@Param("status") String status, Pageable pageable);
}
