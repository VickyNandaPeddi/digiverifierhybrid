package com.aashdit.digiverifier.config.candidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.candidate.model.OrganisationScope;

@Repository
public interface OrganisationScopeRepository extends JpaRepository<OrganisationScope, Long>{

	OrganisationScope findByCandidateId(Long candidateId);
	
}
