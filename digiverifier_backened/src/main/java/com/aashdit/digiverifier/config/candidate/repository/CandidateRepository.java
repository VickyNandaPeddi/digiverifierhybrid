package com.aashdit.digiverifier.config.candidate.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDto;
import com.aashdit.digiverifier.config.candidate.model.Candidate;

import jakarta.transaction.Transactional;


@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

	Candidate findByCandidateCode(String candidateCode);
	Candidate findByCandidateId(Long  candidateId);
	
	@Query(value="select * from t_dgv_candidate_basic where candidate_id "
			+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and last_updated_on between :startDate and :endDate) and organization_id=:organizationId",nativeQuery=true) 
	List<Candidate> getCandidateListByOrganizationIdAndStatusAndLastUpdated(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate);
	
	//createdOn
	@Query(value="select * from t_dgv_candidate_basic where candidate_id "
			+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and created_on between :startDate and :endDate) and organization_id=:organizationId",nativeQuery=true) 
	List<Candidate> getCandidateListByOrganizationIdAndStatusAndCreatedOn(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate);
	
	//createdOn
		@Query(value="select * from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_conventional_candidate_status where status_master_id in (:statusIds) and created_on between :startDate and :endDate) and organization_id=:organizationId",nativeQuery=true) 
		List<Candidate> getCandidateListByOrganizationIdAndStatusAndCreatedOnConventional(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate);
		
	
	@Query(value="select * from t_dgv_candidate_basic where candidate_id "
			+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and last_updated_on between :startDate and :endDate) and created_by in (:agentIds)",nativeQuery=true) 
	List<Candidate> getCandidateListByUserIdAndStatusAndLastUpdated(@Param("agentIds")List<Long> agentIds,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate);

	Candidate findByEmailId(String emailId);
	
	Candidate findByapplicantId(String applicantId);

	Candidate findByPanNumberAndCandidateCode(String pan, String candidateCode);
	
	 @Query(value = "SELECT candidate_upload_filename FROM t_dgv_candidate_basic", nativeQuery = true)
	    List<String> getFilename();
	 	 
	 @Query(value = "SELECT * FROM t_dgv_candidate_basic " +
             "WHERE organization_id = :orgId " +
             "AND (candidate_name = :userSearchInput " +
             "OR email_id = :userSearchInput " +
             "OR applicant_id = :userSearchInput " +
             "OR contact_number = :userSearchInput) " +
             "AND created_on BETWEEN :startDate AND :endDate", nativeQuery = true)
List<Candidate> searchAllCandidateByAdmin(
     @Param("orgId") Long orgId,
     @Param("userSearchInput") String userSearchInput,
     @Param("startDate") Date startDate,
     @Param("endDate") String endDate);
	 
	 
	 @Query(value = "SELECT * FROM t_dgv_candidate_basic " +
             "WHERE created_by = :userId " +
             "AND (candidate_name = :userSearchInput " +
             "OR email_id = :userSearchInput " +
             "OR applicant_id = :userSearchInput " +
             "OR contact_number = :userSearchInput) " +
             "AND created_on BETWEEN :startDate AND :endDate", nativeQuery = true)
List<Candidate> searchAllCandidateByAgent(
     @Param("userId") Long userId,
     @Param("userSearchInput") String userSearchInput,
     @Param("startDate") Date startDate,
     @Param("endDate") String endDate);
	 
//	 @Query(value = "SELECT * FROM t_dgv_candidate_basic " +
//	         "WHERE created_by = :userId " +
//	         "AND ((candidate_name = :userSearchInput " +
//	         "OR email_id = :userSearchInput " +
//	         "OR applicant_id = :userSearchInput " +
//	         "OR contact_number = :userSearchInput) " +
//	         "AND conventional_candidate = true " +
//	         "AND created_on BETWEEN :startDate AND :endDate)", nativeQuery = true)
//	List<Candidate> conventionalSearchAllCandidateByAgent(
//	 @Param("userId") Long userId,
//	 @Param("userSearchInput") String userSearchInput,
//	 @Param("startDate") Date startDate,
//	 @Param("endDate") String endDate);
	 
	 
		@Query(value = "DELETE ccaf, ccad, ccae, ccea, ccd, cces, ccepfo, ccepfor, cci, ccitr, ccitrres, ccru, ccst, ccsth, ccvs, cc\r\n"
				+ "FROM t_dgv_candidate_caf_addcomments AS ccaf\r\n"
				+ "LEFT JOIN t_dgv_candidate_basic AS cb ON ccaf.candidate_id = cb.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_caf_address AS ccad ON ccaf.candidate_id = ccad.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_caf_education AS ccae ON ccaf.candidate_id = ccae.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_caf_experience AS ccea ON ccaf.candidate_id = ccea.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_case_details AS ccd ON ccaf.candidate_id = ccd.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_email_status AS cces ON ccaf.candidate_id = cces.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_epfo AS ccepfo ON ccaf.candidate_id = ccepfo.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_epfo_response AS ccepfor ON ccaf.candidate_id = ccepfor.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_id_items AS cci ON ccaf.candidate_id = cci.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_itr AS ccitr ON ccaf.candidate_id = ccitr.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_itr_response AS ccitrres ON ccaf.candidate_id = ccitrres.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_resume_upload AS ccru ON ccaf.candidate_id = ccru.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_status AS ccst ON ccaf.candidate_id = ccst.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_status_history AS ccsth ON ccaf.candidate_id = ccsth.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_candidate_verification_state AS ccvs ON ccaf.candidate_id = ccvs.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_content AS cc ON ccaf.candidate_id = cc.candidate_id\r\n"
				+ "LEFT JOIN t_dgv_vendor_checks AS cvc ON ccaf.candidate_id = cvc.candidate_id\r\n"
				+ "WHERE cb.organization_id = ?1;\r\n", nativeQuery = true) 
		void deleteAllByOrgId(Long orgId);
		
		@Query(value="Delete from t_dgv_candidate_basic where organization_id = ?1", nativeQuery = true)
		void deleteByOrgId(Long orgId);
	 
	 

	    List<Candidate> findByCreatedOnBefore(Date date);
	    
	    @Query(value="SELECT * FROM digiverifier_techM.t_dgv_candidate_basic WHERE created_on < ?1 and organization_id=?2 and purged_on is null", nativeQuery = true)
	    List<Candidate> findByCreatedOnBeforeAndOrganizationOrganizationId(Date date,Long orgId);


//	    @Transactional
//	    @Modifying
//	    @Query(value = "DELETE t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20 " +
//                "FROM digiverifier_techM.t_dgv_candidate_basic AS main " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_adress_verification t1 ON main.candidate_id = t1.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_caf_addcomments t2 ON main.candidate_id = t2.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_caf_address t3 ON main.candidate_id = t3.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_caf_education t4 ON main.candidate_id = t4.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_caf_experience t5 ON main.candidate_id = t5.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_case_details t6 ON main.candidate_id = t6.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_email_status t7 ON main.candidate_id = t7.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_epfo t8 ON main.candidate_id = t8.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_epfo_response t9 ON main.candidate_id = t9.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_id_items t10 ON main.candidate_id = t10.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_itr t11 ON main.candidate_id = t11.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_itr_response t12 ON main.candidate_id = t12.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_resume_upload t13 ON main.candidate_id = t13.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_status t14 ON main.candidate_id = t14.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_status_history t15 ON main.candidate_id = t15.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_verification_state t16 ON main.candidate_id = t16.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_content t17 ON main.candidate_id = t17.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_vendor_checks t18 ON main.candidate_id = t18.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_remittance t19 ON main.candidate_id = t19.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_gst_data t20 ON main.candidate_id = t20.candidate_id " +
//                "WHERE main.candidate_id IN :candidateIds", nativeQuery = true)
//	    void deleteCandidatesAndRelatedRecords(List<Long> candidateIds);

	    
	    //To get the New upload counts for Organization admin
	    @Query(value="select COUNT(candidate_id) from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and created_on between :startDate and :endDate) and organization_id=:organizationId",nativeQuery=true) 
		int getNewUploadCountByOrganizationIdAndStatusAndCreatedOn(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate);
		
	  //To get the New upload counts for Organization AGENTS
	    @Query(value="select COUNT(candidate_id) from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and last_updated_on between :startDate and :endDate) and created_by in (:agentIds)",nativeQuery=true) 
		int getNewUploadCountByUserIdAndStatusAndLastUpdated(@Param("agentIds")List<Long> agentIds,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate);

	    //below 3 queries are used for getting candidate list by pagination
	    //1
	    @Query(value="select * from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and last_updated_on between :startDate and :endDate) and organization_id=:organizationId ORDER BY created_on DESC",nativeQuery=true) 
		List<Candidate> getPageCandidateListByOrganizationIdAndStatusAndLastUpdated(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);
		
		//2 createdOn
		@Query(value="select * from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and created_on between :startDate and :endDate) and organization_id=:organizationId ORDER BY created_on DESC",nativeQuery=true) 
		List<Candidate> getPageCandidateListByOrganizationIdAndStatusAndCreatedOn(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);
		
		@Query(value="select * from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_conventional_candidate_status where status_master_id in (:statusIds) and created_on between :startDate and :endDate) and organization_id=:organizationId ORDER BY created_on DESC",nativeQuery=true) 
		List<Candidate> getPageCandidateListByOrganizationIdAndStatusAndCreatedOnConventional(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);
		
		
		//3
		@Query(value="select * from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and last_updated_on between :startDate and :endDate) and created_by in (:agentIds)  ORDER BY created_on DESC",nativeQuery=true) 
		List<Candidate> getPageCandidateListByUserIdAndStatusAndLastUpdated(@Param("agentIds")List<Long> agentIds,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);
		
		@Query(value="select * from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_conventional_candidate_status where status_master_id in (:statusIds) and last_updated_on between :startDate and :endDate) and created_by in (:agentIds)  ORDER BY created_on DESC",nativeQuery=true) 
		List<Candidate> getPageCandidateListByUserIdAndStatusAndLastUpdatedConventional(@Param("agentIds")List<Long> agentIds,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);

		@Query(value = "select * from t_dgv_candidate_basic where candidate_name = :userSearchInput OR applicant_id = :userSearchInput",nativeQuery=true)
		List<Long> getByApplicantIdAndCandidateName(@Param("userSearchInput") String userSearchInput);
		

		@Query(value="select * from t_dgv_candidate_basic where candidate_id "
	            + "in (select candidate_id from t_dgv_candidate_status_history where status_master_id in (:statusIds) and candidate_status_change_timestamp between :startDate and :endDate) "
	            + "and candidate_id in (select candidate_id from t_dgv_candidate_basic where is_uan_skipped=true) and organization_id=:organizationId ORDER BY created_on DESC",nativeQuery=true)
		List<Candidate> getPageEPFOSkippedCandidateListForAdmin(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);
		
		@Query(value="select * from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_candidate_status_history where status_master_id in (:statusIds) and candidate_status_change_timestamp between :startDate and :endDate) "
				+ "and candidate_id in (select candidate_id from t_dgv_candidate_basic where is_uan_skipped=true) and created_by in (:agentIds)  ORDER BY created_on DESC",nativeQuery=true) 
		List<Candidate> getPageEPFOSkippedCandidateListForAgent(@Param("agentIds")List<Long> agentIds,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);
		
		//To get the candidate tracker report
	    @Query(value="select * from t_dgv_candidate_basic where created_on between ?2 and ?3 and organization_id in (?1)",nativeQuery=true) 
	    List<Candidate> getCandidatesByCreatedOnAndOrganization(@Param("organizationIds")List<Long> organizationIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate);
	    
	    @Transactional
	    @Modifying
	    @Query(value = "DELETE t1, t2, t3, t4 " +
                "FROM digiverifier_techM.t_dgv_candidate_basic AS main " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_adress_verification t1 ON main.candidate_id = t1.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_caf_addcomments t2 ON main.candidate_id = t2.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_caf_address t3 ON main.candidate_id = t3.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_caf_education t4 ON main.candidate_id = t4.candidate_id " +
                "WHERE main.candidate_id IN :candidateIds", nativeQuery = true)
	    void purgeCandidateTableGroup1(List<Long> candidateIds);
	    
	    @Transactional
	    @Modifying
	    @Query(value = "DELETE t5, t6, t7, t8 " +
                "FROM digiverifier_techM.t_dgv_candidate_basic AS main " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_caf_experience t5 ON main.candidate_id = t5.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_case_details t6 ON main.candidate_id = t6.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_email_status t7 ON main.candidate_id = t7.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_epfo t8 ON main.candidate_id = t8.candidate_id " +
                "WHERE main.candidate_id IN :candidateIds", nativeQuery = true)
	    void purgeCandidateTableGroup2(List<Long> candidateIds);
	    
	    @Transactional
	    @Modifying
	    @Query(value = "DELETE t9, t10, t11, t12 " +
                "FROM digiverifier_techM.t_dgv_candidate_basic AS main " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_epfo_response t9 ON main.candidate_id = t9.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_id_items t10 ON main.candidate_id = t10.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_itr t11 ON main.candidate_id = t11.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_itr_response t12 ON main.candidate_id = t12.candidate_id " +
                "WHERE main.candidate_id IN :candidateIds", nativeQuery = true)
	    void purgeCandidateTableGroup3(List<Long> candidateIds);
	    
	    @Transactional
	    @Modifying
	    @Query(value = "DELETE t13, t16 " +
                "FROM digiverifier_techM.t_dgv_candidate_basic AS main " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_resume_upload t13 ON main.candidate_id = t13.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_status t14 ON main.candidate_id = t14.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_candidate_status_history t15 ON main.candidate_id = t15.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_verification_state t16 ON main.candidate_id = t16.candidate_id " +
                "WHERE main.candidate_id IN :candidateIds", nativeQuery = true)
	    void purgeCandidateTableGroup4(List<Long> candidateIds);
	    
	    @Transactional
	    @Modifying
	    @Query(value = "DELETE t17, t19, t20 " +
                "FROM digiverifier_techM.t_dgv_candidate_basic AS main " +
                "LEFT JOIN digiverifier_techM.t_dgv_content t17 ON main.candidate_id = t17.candidate_id " +
//                "LEFT JOIN digiverifier_techM.t_dgv_vendor_checks t18 ON main.candidate_id = t18.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_remittance t19 ON main.candidate_id = t19.candidate_id " +
                "LEFT JOIN digiverifier_techM.t_dgv_candidate_gst_data t20 ON main.candidate_id = t20.candidate_id " +
                "WHERE main.candidate_id IN :candidateIds", nativeQuery = true)
	    void purgeCandidateTableGroup5(List<Long> candidateIds);
	    
	    @Query(value="select * from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and last_updated_on between :startDate and :endDate) and organization_id=:organizationId and uan is null ORDER BY created_on DESC",nativeQuery=true) 
	    List<Candidate> getPageCandidateListByOrganizationIdAndStatusAndLastUpdatedForFailedPanToUan(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate);
//	    List<Candidate> getPageCandidateListByOrganizationIdAndStatusAndLastUpdatedForFailedPanToUan(@Param("organizationId")Long organizationId,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);
	    
		@Query(value="select * from t_dgv_candidate_basic where candidate_id "
				+ "in (select candidate_id from t_dgv_candidate_status where status_master_id in (:statusIds) and last_updated_on between :startDate and :endDate) and created_by in (:agentIds) and uan is null ORDER BY created_on DESC",nativeQuery=true) 
		List<Candidate> getPageCandidateListByUserIdAndStatusAndLastUpdatedForFailedPanToUan(@Param("agentIds")List<Long> agentIds,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate);
//		List<Candidate> getPageCandidateListByUserIdAndStatusAndLastUpdatedForFailedPanToUan(@Param("agentIds")List<Long> agentIds,@Param("statusIds")List<Long> statusIds,@Param("startDate")Date startDate,@Param("endDate")Date endDate,Pageable pageable);
		
		@Query("SELECT cs.candidate FROM CandidateStatus cs WHERE cs.statusMaster.statusCode in (:statusCodes) and cs.lastUpdatedOn < :cutoffDate AND cs.candidate.organization.organizationId = :orgId")
	    List<Candidate> findCandidatesByLastUpdatedOnAndOrgId(@Param("cutoffDate") Date cutoffDate, @Param("orgId") Long orgId, @Param("statusCodes") List<String> statusCodes);
		
		@Query("SELECT c.candidateId FROM Candidate c WHERE c.organization.organizationId in "
				+ "(SELECT stc.organization.organizationId FROM ServiceTypeConfig stc where stc.serviceSourceMaster.serviceCode=:serviceCode) "
				+ "and c.uan is not null and c.uan <> ''")
	    List<Long> findCandidateIdssBySourceServiceCode(@Param("serviceCode") String serviceCode);
		
		@Query("SELECT c.candidateCode FROM Candidate c WHERE c.candidateId IN :candidateIds AND c.candidateId NOT IN "
				+ "(SELECT ed.candidate.candidateId FROM EpfoData ed)")
	    List<String> findCandidateCodesNotInEpfo(@Param("candidateIds") List<Long> candidateIds);
}
