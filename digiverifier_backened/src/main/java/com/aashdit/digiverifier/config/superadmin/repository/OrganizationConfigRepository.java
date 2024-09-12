package com.aashdit.digiverifier.config.superadmin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.superadmin.model.OrganizationConfig;
import com.amazonaws.services.rds.model.Option;

@Repository
public interface OrganizationConfigRepository extends JpaRepository<OrganizationConfig, Long>{

	Optional<OrganizationConfig> findByOrganizationId(Long organizationId);
	
	@Modifying
	void deleteByOrganizationId(Long orgId);

}
