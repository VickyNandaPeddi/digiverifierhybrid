package com.aashdit.digiverifier.config.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.config.candidate.dto.CandidateCafAddressInterfaceDto;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafAddress;

public interface CandidateCafAddressRepository extends JpaRepository<CandidateCafAddress, Long> {

	List<CandidateCafAddress> findAllByCandidateCandidateCode(String candidateCode);

	CandidateCafAddress findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(String candidateCode,	String serviceCode);

	@Query(value="select distinct (color.colorName) from CandidateCafAddress where candidate.candidateCode =:candidateCode")
	List<String> findDistinctColors(@Param("candidateCode") String candidateCode);

	@Query(value="select count(*) from CandidateCafAddress where candidate.candidateCode =:candidateCode and addressVerification is not null")
	Long findCountByCandidateCodeAndRelAddrVerification(@Param("candidateCode") String candidateCode);

	@Query(value = "SELECT r.candidate_relationship FROM t_dgv_candidate_caf_address a inner join t_dgv_candidate_adress_verification v on a.address_verification_id = v.candidate_address_verification_id inner join t_dgv_candidate_caf_relationship r on r.candidate_relationship_id = v.candidate_relationship_id where a.candidate_id = ?1", nativeQuery = true)
	CandidateCafAddressInterfaceDto findByCandidateCandidateCodeAndAddressVerificationIsNotNull(Long candidateId);

	@Query(value = "SELECT a.candidate_address as candidateAddress FROM t_dgv_candidate_caf_address a inner join t_dgv_source_service_master s on s.source_service_id = a.source_service_id inner join t_dgv_candidate_basic c on c.candidate_id = a.candidate_id where c.candidate_code = ?1 and s.service_code = ?2", nativeQuery = true)
	CandidateCafAddressInterfaceDto findByCandidateCandidateCodeAndServiceSourceMasterServiceCodeAndAddressVerificationIsNull(String candidateCode, String string);

}
