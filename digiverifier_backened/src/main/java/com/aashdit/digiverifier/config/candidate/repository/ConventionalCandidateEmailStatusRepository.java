/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aashdit.digiverifier.config.candidate.model.CandidateEmailStatus;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateEmailStatus;

/**
 * Nambi
 */
public interface ConventionalCandidateEmailStatusRepository extends JpaRepository<ConventionalCandidateEmailStatus, Long> {

	ConventionalCandidateEmailStatus findByCandidateCandidateCode(String candidateCode);

}
