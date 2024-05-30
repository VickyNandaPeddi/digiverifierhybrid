/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aashdit.digiverifier.config.candidate.model.ConventionalLoaConsentMaster;
import com.aashdit.digiverifier.config.candidate.model.LoaConsentMaster;

/**
 * Nambi
 */
public interface ConventionalLoaConsentRepository extends JpaRepository<ConventionalLoaConsentMaster, Long>{

	ConventionalLoaConsentMaster getByCandidateCandidateCode(String candidateCode);

}
