/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aashdit.digiverifier.config.candidate.model.CandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateVerificationState;

/**
 * Nambi
 */
public interface ConventionalCandidateVerificationStateRepository extends JpaRepository<ConventionalCandidateVerificationState, Long> {

	ConventionalCandidateVerificationState findByCandidateCandidateId(Long candidateId);

}
