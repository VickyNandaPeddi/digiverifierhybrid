package com.aashdit.digiverifier.gst.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.gst.model.GstData;

@Repository
public interface GstRepository extends JpaRepository<GstData, Long>{

	@Query("FROM GstData where candidate.candidateCode =:candidateCode")
	List<GstData> findAllByCandidateCandidateCode(@Param("candidateCode") String candidateCode);
	
	@Query("FROM GstData where candidate.candidateId =:candidateId")
	List<GstData> findAllByCandidate(@Param("candidateId") Long candidateId);
}
