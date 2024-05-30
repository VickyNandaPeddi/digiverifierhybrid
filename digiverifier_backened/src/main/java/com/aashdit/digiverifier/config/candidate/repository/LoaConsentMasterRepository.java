package com.aashdit.digiverifier.config.candidate.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.config.candidate.model.LoaConsentMaster;

public interface LoaConsentMasterRepository extends JpaRepository<LoaConsentMaster, Long> {
	
	@Query("SELECT cm FROM LoaConsentMaster cm " +
		       "JOIN cm.candidate c " +
		       "JOIN c.organization org " +
		       "WHERE org.organizationId IN (:orgId) " +
		       "AND cm.createdOn BETWEEN :startDate AND :endDate")
	List<LoaConsentMaster> getByOrgAndCreatedOn(@Param("orgId")List<Long> orgId,@Param("startDate")Date startDate,@Param("endDate")Date endDate);

	@Query("SELECT cm FROM LoaConsentMaster cm " +
		       "JOIN cm.candidate c " +
		       "JOIN c.createdBy org " +
		       "WHERE org.userId IN (:agentId) " +
		       "AND cm.createdOn BETWEEN :startDate AND :endDate")
	List<LoaConsentMaster> getByAgentAndCreatedOn(@Param("agentId")List<Long> agentId,@Param("startDate")Date startDate,@Param("endDate")Date endDate);
	
	LoaConsentMaster getByCandidateCandidateCode(String candidateCode);
}
