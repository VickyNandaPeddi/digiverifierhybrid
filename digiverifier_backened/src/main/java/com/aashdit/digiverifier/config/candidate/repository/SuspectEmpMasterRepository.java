package com.aashdit.digiverifier.config.candidate.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.config.candidate.model.SuspectEmpMaster;

public interface SuspectEmpMasterRepository extends JpaRepository<SuspectEmpMaster, Long> {

	List<SuspectEmpMaster> findAllByIsActiveTrue();

	List<SuspectEmpMaster> findAllByOrganizationOrganizationIdAndIsActiveTrue(Long organizationId);
	
	@Query(value = "SELECT * FROM t_dgv_suspect_emp_master WHERE organization_id = ?1 AND is_active = true AND suspect_company_name LIKE CONCAT('%', ?2)", nativeQuery = true)
	List<SuspectEmpMaster> getByOrganizationIdAndSuspectCompanyName(Long organizationId, String SuspectCompanyName);

	List<SuspectEmpMaster> findAllByIsActiveTrueOrderBySuspectEmpMasterIdAsc();

	@Query("FROM SuspectEmpMaster where isActive=true AND suspectEmpMasterId > 0 order by suspectCompanyName ASC")
	List<SuspectEmpMaster> findAllByIsActiveTrueOrderBySuspectCompanyNameAsc();

    Page<SuspectEmpMaster> findAllByOrganizationOrganizationId(Long organizationId, Pageable pageable);

	void deleteById(Long id);
	
	//@Query("FROM SuspectEmpMaster where organization_id = :id")
	@Query("SELECT se.suspectCompanyName FROM SuspectEmpMaster se WHERE se.organization.id = :id")
	List<String> getAllSuspectEmpMasterByOrgId(@Param("id") Long id);
	
	@Query("SELECT se FROM SuspectEmpMaster se WHERE se.suspectCompanyName = :userSearchInput AND se.organization.id = :id")
	List<SuspectEmpMaster> searchDnh(@Param("userSearchInput") String userSearchInput, @Param("id") Long id);

	@Modifying
	@Query("DELETE FROM SuspectEmpMaster se WHERE se.organization.id = :orgId")
	void removeAllSuspectEmpByOrgId(@Param("orgId") Long orgId);

}
