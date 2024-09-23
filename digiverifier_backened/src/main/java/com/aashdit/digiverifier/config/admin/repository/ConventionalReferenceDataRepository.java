/**
 * 
 */
package com.aashdit.digiverifier.config.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.ConventionalReferenceData;
import java.util.List;


/**
 * Nambi
 */
public interface ConventionalReferenceDataRepository extends JpaRepository<ConventionalReferenceData, Long>{

	ConventionalReferenceData findByCandidateId(Candidate candidateId);
	
}
