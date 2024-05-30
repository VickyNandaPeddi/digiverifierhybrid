package com.aashdit.digiverifier.config.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.config.admin.model.ConventionalAttributesMaster;

public interface ConventionalAttributesMasterRepository extends JpaRepository<ConventionalAttributesMaster, Long>{

	@Query(value="SELECT * FROM t_dgv_conventional_attributes_master WHERE source_id = :sourceId", nativeQuery = true)
	List<ConventionalAttributesMaster> findBySourceId(@Param("sourceId") Long sourceId);

	@Query(value="SELECT * FROM t_dgv_conventional_attributes_master WHERE source_id = :sourceId AND global_check_type = :type", nativeQuery = true)
	List<ConventionalAttributesMaster> findBySourceIdWithType(@Param("sourceId") Long sourceId,@Param("type") String type);


}
