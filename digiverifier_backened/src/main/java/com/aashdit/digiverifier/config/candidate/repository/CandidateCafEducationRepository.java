package com.aashdit.digiverifier.config.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.config.candidate.model.CandidateCafEducation;

public interface CandidateCafEducationRepository extends JpaRepository<CandidateCafEducation, Long> {

	List<CandidateCafEducation> findAllByCandidateCandidateCode(String candidateCode);

	@Query(value="select distinct (color.colorName) from CandidateCafEducation where candidate.candidateCode =:candidateCode")
	List<String> findDistinctColors(@Param("candidateCode") String candidateCode);
	
	List<CandidateCafEducation> findAllByCandidateCandidateId(Long candidateId);
	
	void deleteById(Long id);
	
	@Query(value="select c from CandidateCafEducation c where c.candidate.candidateCode =:candidateCode and c.qualificationMaster.qualificationCode =:qualificationCode")
	CandidateCafEducation findByCandidateAndQualificationCode(@Param("candidateCode") String candidateCode,@Param("qualificationCode") String qualificationCode);
}
