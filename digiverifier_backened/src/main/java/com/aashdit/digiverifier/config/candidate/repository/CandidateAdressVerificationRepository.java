package com.aashdit.digiverifier.config.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.aashdit.digiverifier.config.candidate.model.CandidateAdressVerification;

import jakarta.transaction.Transactional;

public interface CandidateAdressVerificationRepository extends JpaRepository<CandidateAdressVerification, Long> {

	CandidateAdressVerification findByCandidateCandidateCode(String candidateCode);

	
	@Transactional
    @Modifying                  
    @Query(value = "DELETE FROM CandidateAdressVerification WHERE candidate.candidateId IN :candidateIds")
	void deleteCanAddressVerByCanIds(List<Long> candidateIds);
}
