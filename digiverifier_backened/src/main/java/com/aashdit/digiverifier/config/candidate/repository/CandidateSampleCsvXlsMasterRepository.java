package com.aashdit.digiverifier.config.candidate.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aashdit.digiverifier.config.candidate.model.CandidateSampleCsvXlsMaster;

public interface CandidateSampleCsvXlsMasterRepository extends JpaRepository<CandidateSampleCsvXlsMaster, Long> {
	List<CandidateSampleCsvXlsMaster> findByCreatedOnBeforeAndOrganizationOrganizationId(Date date,Long orgId);

}
