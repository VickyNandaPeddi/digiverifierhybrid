package com.aashdit.digiverifier.config.candidate.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.candidate.dto.CandidateStatusDto;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;

@Repository
public interface CandidateStatusHistoryRepository extends JpaRepository<CandidateStatusHistory, Long> {

	@Query("FROM CandidateStatusHistory  WHERE candidate.organization.organizationId=:organizationId AND candidateStatusChangeTimestamp between :startDate and :endDate")
	List<CandidateStatusHistory> findAllByOrganizationIdAndDateRange(@Param("organizationId")Long organizationId, @Param("startDate")Date startDate,@Param("endDate")Date endDate);

	@Query("FROM CandidateStatusHistory  WHERE candidateStatusChangeTimestamp between :startDate and :endDate")
	List<CandidateStatusHistory> findAllByDateRange( @Param("startDate")Date startDate,@Param("endDate")Date endDate);

	@Query("FROM CandidateStatusHistory  WHERE candidate.organization.organizationId in (:organizationId) AND candidateStatusChangeTimestamp between :startDate and :endDate AND statusMaster.statusCode =:status")
	List<CandidateStatusHistory> findAllByOrganizationIdAndDateRangeAndStatus(@Param("organizationId")List<Long> organizationId, @Param("startDate")Date startDate,@Param("endDate")Date endDate,
			@Param("status")String status);

	@Query("FROM CandidateStatusHistory  WHERE candidate.organization.organizationId =:organizationId AND candidateStatusChangeTimestamp between :startDate and :endDate AND statusMaster.statusCode in (:statusList)")
	List<CandidateStatusHistory> findAllByOrganizationIdAndDateRangeAndStatusCode(@Param("organizationId")Long organizationIds, @Param("startDate")Date startDate,
			@Param("endDate")Date endDate,@Param("statusList")List<String> statusList);
	
	List<CandidateStatusHistory> findAllByCandidateCandidateId(Long candidateId);
	
	@Query("FROM CandidateStatusHistory WHERE candidate.candidateId = :candidateId ORDER BY candidateStatusHistoryId DESC LIMIT 1")
	CandidateStatusHistory findLastStatusHistorytRecord(Long candidateId);
	
//	@Query("FROM CandidateStatusHistory  WHERE candidate.organization.organizationId in (:organizationId) AND candidateStatusChangeTimestamp between :startDate and :endDate AND statusMaster.statusCode in (:statusList)")
	@Query("SELECT DISTINCT csh FROM CandidateStatusHistory csh WHERE csh.candidate.organization.organizationId in (:organizationId) AND csh.candidateStatusChangeTimestamp BETWEEN :startDate AND :endDate AND csh.statusMaster.statusCode IN (:statusList) GROUP BY csh.candidate")
	List<CandidateStatusHistory> findAllByOrganizationIdsAndDateRangeAndStatusList(@Param("organizationId")List<Long> organizationId, @Param("startDate")Date startDate,@Param("endDate")Date endDate,
			@Param("statusList")List<String> statusList);
	
	@Query(value = "SELECT \r\n"
			+ "    CB.candidate_code AS candidateCode,\r\n"
			+ "    CB.candidate_id AS candidateId,\r\n"
			+ "    CB.aadhar_dob AS aadharDob,\r\n"
			+ "    CB.aadhar_name AS aadharName,\r\n"
			+ "    CB.aadhar_number AS aadharNumber,\r\n"
			+ "    CB.aadhar_father_name AS aadharFatherName,\r\n"
			+ "    CB.aadhar_gender AS aadharGender,\r\n"
			+ "    CB.applicant_id AS applicantId,\r\n"
			+ "    CB.candidate_name AS candidateName,\r\n"
			+ "    CB.contact_number AS contactNumber,\r\n"
			+ "    CB.date_of_birth AS dateOfBirth,\r\n"
			+ "    CB.email_id AS emailId,\r\n"
			+ "    CONCAT(CB.experience_in_month, ' Years') AS experience,\r\n"
			+ "    CB.itr_pan_number AS panNumber,\r\n"
			+ "    SM.status_name AS statusName,\r\n"
			+ "    CS.created_on AS statusDate,\r\n"
			+ "    u.user_first_name AS createdByUserFirstName,\r\n"
			+ "    u.user_last_name AS createdByUserLastName,\r\n"
//			+ "    c.color_name AS colorName,\r\n"
			+ "    o.organization_name AS organizationOrganizationName,\r\n"
			+ "    CB.created_on AS createdOn,\r\n"
			+ "    (SELECT \r\n"
			+ "            I.id_holder\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_id_items I\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_source_service_master S ON I.service_source_master_id = S.source_service_id\r\n"
			+ "        WHERE\r\n"
			+ "            I.candidate_id = CB.candidate_id\r\n"
			+ "                AND S.service_code = 'PAN'\r\n"
			+ "        LIMIT 1) AS panName,\r\n"
			+ "    (SELECT \r\n"
			+ "            I.id_holder_dob\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_id_items I\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_source_service_master S ON I.service_source_master_id = S.source_service_id\r\n"
			+ "        WHERE\r\n"
			+ "            I.candidate_id = CB.candidate_id\r\n"
			+ "                AND S.service_code = 'PAN'\r\n"
			+ "        LIMIT 1) AS panDob,\r\n"
			+ "    (SELECT \r\n"
			+ "            GROUP_CONCAT(DISTINCT uan\r\n"
			+ "                    SEPARATOR '/')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_caf_experience\r\n"
			+ "        WHERE\r\n"
			+ "            candidate_id = CB.candidate_id\r\n"
			+ "                AND uan IS NOT NULL) AS candidateUan,\r\n"
			+ "    (SELECT \r\n"
			+ "            a.candidate_address\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_caf_address a\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_source_service_master s ON s.source_service_id = a.source_service_id\r\n"
			+ "        WHERE\r\n"
			+ "            a.candidate_id = CB.candidate_id\r\n"
			+ "        LIMIT 1) AS address,\r\n"
			+ "    (SELECT \r\n"
			+ "            r.candidate_relationship\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_caf_address a\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_candidate_adress_verification v ON a.address_verification_id = v.candidate_address_verification_id\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_candidate_caf_relationship r ON r.candidate_relationship_id = v.candidate_relationship_id\r\n"
			+ "        WHERE\r\n"
			+ "            a.candidate_id = CB.candidate_id\r\n"
			+ "        LIMIT 1) AS relation,\r\n"
			+ "    (SELECT \r\n"
			+ "            name\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_epfo\r\n"
			+ "        WHERE\r\n"
			+ "            candidate_id = CB.candidate_id\r\n"
			+ "        LIMIT 1) AS candidateUanName\r\n"
			+ "FROM\r\n"
			+ "    t_dgv_candidate_status_history CS\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_candidate_basic CB ON CS.candidate_id = CB.candidate_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_status_master SM ON SM.status_master_id = CS.status_master_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_user_master AS u ON u.user_id = CS.created_by\r\n"
//			+ "        LEFT JOIN\r\n"
//			+ "    t_dgv_color_master AS c ON c.color_id = CS.color_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_organization_master o ON o.organization_id = u.orgainzation_id\r\n"
			+ "WHERE\r\n"
			+ "	CS.candidate_status_change_timestamp between ?1 and ?2 and o.organization_id in (?3) and SM.status_code In (?4) GROUP BY CB.candidate_id", nativeQuery = true)
	List<CandidateStatusDto> findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdIn(Date startDate,
			Date endDate, List<Long> organizationIds, List<String> statusList);
	
	@Query(value = "SELECT \r\n"
			+ "    CB.candidate_code AS candidateCode,\r\n"
			+ "    CB.candidate_id AS candidateId,\r\n"
			+ "    CB.aadhar_dob AS aadharDob,\r\n"
			+ "    CB.aadhar_name AS aadharName,\r\n"
			+ "    CB.aadhar_number AS aadharNumber,\r\n"
			+ "    CB.aadhar_father_name AS aadharFatherName,\r\n"
			+ "    CB.aadhar_gender AS aadharGender,\r\n"
			+ "    CB.applicant_id AS applicantId,\r\n"
			+ "    CB.candidate_name AS candidateName,\r\n"
			+ "    CB.contact_number AS contactNumber,\r\n"
			+ "    CB.date_of_birth AS dateOfBirth,\r\n"
			+ "    CB.email_id AS emailId,\r\n"
			+ "    CONCAT(CB.experience_in_month, ' Years') AS experience,\r\n"
			+ "    CB.itr_pan_number AS panNumber,\r\n"
			+ "    SM.status_name AS statusName,\r\n"
			+ "    CS.created_on AS statusDate,\r\n"
			+ "    u.user_first_name AS createdByUserFirstName,\r\n"
			+ "    u.user_last_name AS createdByUserLastName,\r\n"
//			+ "    c.color_name AS colorName,\r\n"
			+ "    o.organization_name AS organizationOrganizationName,\r\n"
			+ "    CB.created_on AS createdOn,\r\n"
			+ "    (SELECT \r\n"
			+ "            I.id_holder\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_id_items I\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_source_service_master S ON I.service_source_master_id = S.source_service_id\r\n"
			+ "        WHERE\r\n"
			+ "            I.candidate_id = CB.candidate_id\r\n"
			+ "                AND S.service_code = 'PAN'\r\n"
			+ "        LIMIT 1) AS panName,\r\n"
			+ "    (SELECT \r\n"
			+ "            I.id_holder_dob\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_id_items I\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_source_service_master S ON I.service_source_master_id = S.source_service_id\r\n"
			+ "        WHERE\r\n"
			+ "            I.candidate_id = CB.candidate_id\r\n"
			+ "                AND S.service_code = 'PAN'\r\n"
			+ "        LIMIT 1) AS panDob,\r\n"
			+ "    (SELECT \r\n"
			+ "            GROUP_CONCAT(DISTINCT uan\r\n"
			+ "                    SEPARATOR '/')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_caf_experience\r\n"
			+ "        WHERE\r\n"
			+ "            candidate_id = CB.candidate_id\r\n"
			+ "                AND uan IS NOT NULL) AS candidateUan,\r\n"
			+ "    (SELECT \r\n"
			+ "            a.candidate_address\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_caf_address a\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_source_service_master s ON s.source_service_id = a.source_service_id\r\n"
			+ "        WHERE\r\n"
			+ "            a.candidate_id = CB.candidate_id\r\n"
			+ "        LIMIT 1) AS address,\r\n"
			+ "    (SELECT \r\n"
			+ "            r.candidate_relationship\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_caf_address a\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_candidate_adress_verification v ON a.address_verification_id = v.candidate_address_verification_id\r\n"
			+ "                LEFT JOIN\r\n"
			+ "            t_dgv_candidate_caf_relationship r ON r.candidate_relationship_id = v.candidate_relationship_id\r\n"
			+ "        WHERE\r\n"
			+ "            a.candidate_id = CB.candidate_id\r\n"
			+ "        LIMIT 1) AS relation,\r\n"
			+ "    (SELECT \r\n"
			+ "            name\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_epfo\r\n"
			+ "        WHERE\r\n"
			+ "            candidate_id = CB.candidate_id\r\n"
			+ "        LIMIT 1) AS candidateUanName\r\n"
			+ "FROM\r\n"
			+ "    t_dgv_candidate_status_history CS\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_candidate_basic CB ON CS.candidate_id = CB.candidate_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_status_master SM ON SM.status_master_id = CS.status_master_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_user_master AS u ON u.user_id = CS.created_by\r\n"
//			+ "        LEFT JOIN\r\n"
//			+ "    t_dgv_color_master AS c ON c.color_id = CS.color_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_organization_master o ON o.organization_id = u.orgainzation_id\r\n"
			+ "WHERE\r\n"
			+ "	CS.candidate_status_change_timestamp between ?1 and ?2 and o.organization_id in (?3) and CB.created_by in (?4) and SM.status_code In (?4) GROUP BY CB.candidate_id", nativeQuery = true)
	List<CandidateStatusDto> findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdInAndCreatedByUserIdIn(
			Date startDate, Date endDate, List<Long> organizationIds, List<Long> agentIds, List<String> statusList);

			@Query("SELECT COUNT(DISTINCT csh.candidate.id) " +
		       "FROM CandidateStatusHistory csh " +
		       "WHERE csh.candidate.organization.organizationId IN (:organizationId) " +
		       "AND csh.candidate.createdBy.userId IN (:userId) " +
		       "AND csh.candidateStatusChangeTimestamp BETWEEN :startDate AND :endDate " +
		       "AND csh.statusMaster.statusCode = :status")
		Integer countDistinctCandidateStatusHistory(@Param("organizationId") List<Long> organizationId,
		                                         @Param("userId") List<Long> userId,
		                                         @Param("startDate") Date startDate,
		                                         @Param("endDate") Date endDate,
		                                         @Param("status") String status);
	
	@Query("SELECT COUNT(DISTINCT csh.candidate.id) " +
		       "FROM CandidateStatusHistory csh " +
		       "WHERE csh.candidate.organization.organizationId IN (:organizationId) " +
		       "AND csh.candidateStatusChangeTimestamp BETWEEN :startDate AND :endDate " +
		       "AND csh.statusMaster.statusCode = :status")
		Integer countDistinctCandidateStatusHistoryByOrg(@Param("organizationId") List<Long> organizationId,
		                                         @Param("startDate") Date startDate,
		                                         @Param("endDate") Date endDate,
		                                         @Param("status") String status);
}
