package com.aashdit.digiverifier.config.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.aashdit.digiverifier.config.admin.model.AgentSampleCsvXlsMaster;

public interface AgentSampleCsvXlsMasterRepository extends JpaRepository<AgentSampleCsvXlsMaster, Long> {

	@Modifying
	void deleteByOrganizationOrganizationId(Long orgId);

}
