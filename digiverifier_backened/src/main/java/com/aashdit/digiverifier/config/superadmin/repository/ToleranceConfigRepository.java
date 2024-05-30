package com.aashdit.digiverifier.config.superadmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.aashdit.digiverifier.config.superadmin.model.ToleranceConfig;

public interface ToleranceConfigRepository extends JpaRepository<ToleranceConfig, Long> {

	ToleranceConfig findByOrganizationOrganizationId(Long organizationId);

	@Modifying
	void deleteByOrganizationOrganizationId(Long orgId);

}
