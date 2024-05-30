package com.aashdit.digiverifier.config.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.candidate.model.CandidateCafRelationship;

import jakarta.transaction.Transactional;

@Repository
public interface CandidateCafRelationshipRepository extends JpaRepository<CandidateCafRelationship, Long> {
	
	@Transactional
    @Modifying                  
    @Query(value = "DELETE FROM CandidateCafRelationship WHERE candidateStatus.candidateStatusId IN :candidateStatusIds")
	void deleteCafRelRecordsByCanStatusIds(List<Long> candidateStatusIds);

}
