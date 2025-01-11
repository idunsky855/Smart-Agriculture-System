package aii.dal;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aii.data.ObjectEntity;

public interface ObjectsCrud extends JpaRepository<ObjectEntity, String> {

	public List<ObjectEntity> findAllByTypeIgnoreCaseAndActiveTrue(@Param("type") String type, Pageable pageable);

	public List<ObjectEntity> findAllByTypeAndStatusIgnoreCaseAndActiveTrue(@Param("type") String type,
			@Param("status") String status, Pageable pageable);

	public List<ObjectEntity> findAllByTypeIgnoreCase(@Param("type") String type, Pageable pageable);

	public List<ObjectEntity> findAllByTypeAndStatusIgnoreCase(@Param("type") String type,
			@Param("status") String status, Pageable pageable);

	public List<ObjectEntity> findAllByActiveTrue(Pageable pageable);

	@Query(value = "SELECT *, " +
			"SQRT(POW((:centerLat - lat), 2) + POW((:centerLng - lng), 2)) AS distance " +
			"FROM objects " +
			"WHERE SQRT(POW((:centerLat - lat), 2) + POW((:centerLng - lng), 2)) <= :radius AND active = TRUE "
			+ "ORDER BY distance", nativeQuery = true)
	public List<ObjectEntity> findAllWithinRadiusAndActiveIsTrue(@Param("centerLat") double centerLat,
			@Param("centerLng") double centerLng,
			@Param("radius") double radius,
			Pageable pageable); // NEUTRAL and active

	@Query(value = "SELECT *, " +
			"SQRT(POW((:centerLat - lat), 2) + POW((:centerLng - lng), 2)) AS distance " +
			"FROM objects " +
			"WHERE SQRT(POW((:centerLat - lat), 2) + POW((:centerLng - lng), 2)) <= :radius " +
			"ORDER BY distance", nativeQuery = true)
	public List<ObjectEntity> findAllWithinRadius(@Param("centerLat") double centerLat,
			@Param("centerLng") double centerLng,
			@Param("radius") double radius,
			Pageable pageable); // NEUTRAL

	@Query(value = "SELECT *, " +
			"(6371 * ACOS(COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS(:centerLng)) + SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat)))) AS distance "
			+
			"FROM objects " +
			"WHERE (6371 * ACOS(COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS(:centerLng)) + SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat)))) <= :radius "
			+
			"ORDER BY distance", nativeQuery = true)
	public List<ObjectEntity> findAllWithinRadiusKm(@Param("centerLat") double centerLat,
			@Param("centerLng") double centerLng,
			@Param("radius") double radius, Pageable pageable); // KILOMETERS

	@Query(value = "SELECT *, " +
			"(6371 * ACOS(COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS(:centerLng)) + SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat)))) AS distance "
			+
			"FROM objects " +
			"WHERE (6371 * ACOS(COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) * COS(RADIANS(lng) - RADIANS(:centerLng)) + SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat)))) <= :radius AND active = TRUE "
			+
			"ORDER BY distance", nativeQuery = true)
	public List<ObjectEntity> findAllWithinRadiusKmAndActiveIsTrue(@Param("centerLat") double centerLat,
			@Param("centerLng") double centerLng,
			@Param("radius") double radius, Pageable pageable); // KILOMETERS and active
}