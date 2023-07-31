package com.aashdit.digiverifier.config.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aashdit.digiverifier.config.candidate.dto.IdItemsDto;
import com.aashdit.digiverifier.config.candidate.model.CandidateIdItems;

public interface CandidateIdItemsRepository extends JpaRepository<CandidateIdItems, Long> {

	List<CandidateIdItems> findByCandidateCandidateCode(String candidateCode);

	@Query(value = "SELECT I.id_holder as idHolder, I.id_holder_dob as idHolderDob FROM t_dgv_candidate_id_items I inner join t_dgv_source_service_master S on I.service_source_master_id = S.source_service_id inner join t_dgv_candidate_basic C on C.candidate_id = I.candidate_id where C.candidate_code = ?1 and S.service_code = ?2 ;", nativeQuery = true)
	IdItemsDto findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(String candidateCode, String serviceCode);

}
