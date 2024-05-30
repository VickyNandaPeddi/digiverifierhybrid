/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatusHistory;

/**
 * Nambi
 */
public interface ConventionalCandidateStatusHistoryRepository extends JpaRepository<ConventionalCandidateStatusHistory, Long> {

	List<ConventionalCandidateStatusHistory> findAllByCandidateCandidateId(Long candidateId);

	@Query("FROM ConventionalCandidateStatusHistory WHERE candidate.candidateId = :candidateId ORDER BY candidateStatusHistoryId DESC LIMIT 1")
	ConventionalCandidateStatusHistory findLastStatusHistorytRecord(Long candidateId);
	
//	@Query(value = "SELECT * FROM t_dgv_conventional_candidate_status_history WHERE candidate_id = :candidateId ORDER BY conventional_candidate_status_history_id DESC LIMIT 1", nativeQuery = true)
//	ConventionalCandidateStatusHistory findLastStatusHistorytRecord(@Param("candidateId") Long candidateId);
}
