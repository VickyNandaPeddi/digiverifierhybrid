package com.aashdit.digiverifier.config.superadmin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.config.superadmin.model.Source;

public interface SourceRepository extends JpaRepository<Source, Long> {

	Source findBySourceName(String sourceName);

	List<Source> findByIsActiveTrue();
	
	 @Query("SELECT s FROM Source s WHERE s.sourceId IN :particularSourceIds")
	    List<Source> findByParticularSourceIdsIn(@Param("particularSourceIds") List<Long> particularSourceIds);
}
