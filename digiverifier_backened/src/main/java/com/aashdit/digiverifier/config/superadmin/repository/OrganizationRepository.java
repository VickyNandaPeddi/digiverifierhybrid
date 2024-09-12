package com.aashdit.digiverifier.config.superadmin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.superadmin.model.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

	List<Organization> findAllByIsActive(boolean isActive);
	
	List<Organization> findByOrganizationName(String orgName);
	
	List<Organization> findByOrganizationEmailId(String orgEmailId);
}
