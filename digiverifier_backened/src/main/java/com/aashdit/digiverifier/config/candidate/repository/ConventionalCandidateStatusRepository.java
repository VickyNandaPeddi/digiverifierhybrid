/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatus;

/**
 * Nambi
 */
public interface ConventionalCandidateStatusRepository extends JpaRepository<ConventionalCandidateStatus, Long> {

	ConventionalCandidateStatus findByCandidateCandidateCode(String candidateCode);
	
	ConventionalCandidateStatus findByCandidateCandidateId(Long Id);
	
}
