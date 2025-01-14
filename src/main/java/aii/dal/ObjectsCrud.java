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

    public List<ObjectEntity> findAllByAlias(@Param("alias") String alias, Pageable pageable);

    public List<ObjectEntity> findAllByAliasLike(@Param("pattern") String pattern, Pageable pageable);

	public List<ObjectEntity> findAllByAliasAndActiveTrue(@Param("alias") String alias, Pageable pageable);

	public List<ObjectEntity> findAllByAliasLikeAndActiveTrue(@Param("pattern") String pattern, Pageable pageable);

	public List<ObjectEntity> findAllByActiveTrue(Pageable pageable);

	@Query(value = """
		SELECT *, 
			CASE :unit
				WHEN 'NEUTRAL' THEN SQRT(POWER((:centerLat - lat), 2) + POWER((:centerLng - lng), 2))
				WHEN 'KILOMETERS' THEN 6371 * ACOS(
					COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) *
					COS(RADIANS(lng) - RADIANS(:centerLng)) +
					SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat))
				)
				WHEN 'MILES' THEN 3958.8 * ACOS(
					COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) *
					COS(RADIANS(lng) - RADIANS(:centerLng)) +
					SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat))
				)
			END AS distance
		FROM objects
		WHERE
			CASE :unit
				WHEN 'NEUTRAL' THEN SQRT(POWER((:centerLat - lat), 2) + POWER((:centerLng - lng), 2))
				WHEN 'KILOMETERS' THEN 6371 * ACOS(
					COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) *
					COS(RADIANS(lng) - RADIANS(:centerLng)) +
					SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat))
				)
				WHEN 'MILES' THEN 3958.8 * ACOS(
					COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) *
					COS(RADIANS(lng) - RADIANS(:centerLng)) +
					SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat))
				)
			END <= :radius
		ORDER BY distance ASC, object_id ASC
		""", nativeQuery = true)
	List<ObjectEntity> findAllWithinRadius(
		@Param("centerLat") double centerLat,
		@Param("centerLng") double centerLng,
		@Param("radius") double radius,
		@Param("unit") String unit,  // NEUTRAL, KILOMETERS, MILES
		Pageable pageable
	);

	@Query(value = """
		SELECT *, 
			CASE :unit
				WHEN 'NEUTRAL' THEN SQRT(POWER((:centerLat - lat), 2) + POWER((:centerLng - lng), 2))
				WHEN 'KILOMETERS' THEN 6371 * ACOS(
					COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) *
					COS(RADIANS(lng) - RADIANS(:centerLng)) +
					SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat))
				)
				WHEN 'MILES' THEN 3958.8 * ACOS(
					COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) *
					COS(RADIANS(lng) - RADIANS(:centerLng)) +
					SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat))
				)
			END AS distance
		FROM objects
		WHERE
			CASE :unit
				WHEN 'NEUTRAL' THEN SQRT(POWER((:centerLat - lat), 2) + POWER((:centerLng - lng), 2))
				WHEN 'KILOMETERS' THEN 6371 * ACOS(
					COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) *
					COS(RADIANS(lng) - RADIANS(:centerLng)) +
					SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat))
				)
				WHEN 'MILES' THEN 3958.8 * ACOS(
					COS(RADIANS(:centerLat)) * COS(RADIANS(lat)) *
					COS(RADIANS(lng) - RADIANS(:centerLng)) +
					SIN(RADIANS(:centerLat)) * SIN(RADIANS(lat))
				)
			END <= :radius
			AND active = TRUE
		ORDER BY distance ASC, object_id ASC
		""", nativeQuery = true)
	List<ObjectEntity> findAllWithinRadiusAndActiveIsTrue(
		@Param("centerLat") double centerLat,
		@Param("centerLng") double centerLng,
		@Param("radius") double radius,
		@Param("unit") String unit,  // NEUTRAL, KILOMETERS, MILES
		Pageable pageable
	);
	
	
}
