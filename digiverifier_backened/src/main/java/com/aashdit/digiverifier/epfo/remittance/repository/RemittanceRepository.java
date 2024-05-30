package com.aashdit.digiverifier.epfo.remittance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.epfo.remittance.model.RemittanceData;

@Repository
public interface RemittanceRepository extends JpaRepository<RemittanceData, Long>{

	@Query("FROM RemittanceData where candidate.candidateCode =:candidateCode")
	List<RemittanceData> findAllByCandidateCandidateCode(@Param("candidateCode") String candidateCode);
	
	@Query("FROM RemittanceData rd where rd.candidate.candidateCode =:candidateCode and rd.memberId=:memberId and rd.year=:year")
	List<RemittanceData> findAllByCandidateCandidateCodeAndMemberIdAndYear(@Param("candidateCode") String candidateCode,
			@Param("memberId") String memberId,@Param("year") String year);
}
