package com.aashdit.digiverifier.config.superadmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.superadmin.model.OrganizationEmailTemplate;


@Repository
public interface OrganizationEmailTemplateRepository extends JpaRepository<OrganizationEmailTemplate ,Long> {

	OrganizationEmailTemplate getByOrganizationId(Long orgId);

	OrganizationEmailTemplate getByEmailTempId(Long emailTemplateId);

}
