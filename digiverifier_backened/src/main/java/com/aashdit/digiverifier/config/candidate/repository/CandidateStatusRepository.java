package com.aashdit.digiverifier.config.candidate.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.candidate.dto.CandidateStatusDto;
import com.aashdit.digiverifier.config.candidate.dto.candidatePurgedReportDto;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;

@Repository
public interface CandidateStatusRepository extends JpaRepository<CandidateStatus, Long> {

	CandidateStatus findByCandidateCandidateCode(String candidateCode);

	List<CandidateStatus> findAllByStatusMasterStatusCode(String status);

	List<CandidateStatus> findAllByCandidateOrganizationOrganizationIdAndLastUpdatedOnBetween(Long organizationId,
			Date startDate, Date endDate);
	
	List<CandidateStatus> findAllByCandidateCreatedByUserIdInAndLastUpdatedOnBetween(List<Long> agentIds, Date startDate,
			Date endDate);

	List<CandidateStatus> findAllByStatusMasterStatusCodeIn(List<String> statusList);

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
			+ "    c.color_name AS colorName,\r\n"
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
			+ "    t_dgv_candidate_status CS\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_candidate_basic CB ON CS.candidate_id = CB.candidate_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_status_master SM ON SM.status_master_id = CS.status_master_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_user_master AS u ON u.user_id = CS.created_by\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_color_master AS c ON c.color_id = CS.color_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_organization_master o ON o.organization_id = u.orgainzation_id\r\n"
			+ "WHERE\r\n"
			+ "	CS.created_on between ?1 and ?2 and o.organization_id in (?3)", nativeQuery = true)
	List<CandidateStatusDto> findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdIn(Date startDate,
			Date endDate, List<Long> organizationIds);

	@Query("FROM CandidateStatus  WHERE candidate.organization.organizationId=:organizationId AND lastUpdatedOn between :startDate and :endDate")
	List<CandidateStatus> findAllByOrganizationIdAndDateRange(@Param("organizationId")Long organizationId, @Param("startDate")Date startDate,@Param("endDate")Date endDate);

	@Query("FROM CandidateStatus  WHERE lastUpdatedOn between :startDate and :endDate")
	List<CandidateStatus> findAllByDateRange( @Param("startDate")Date startDate,@Param("endDate")Date endDate);

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
			+ "    c.color_name AS colorName,\r\n"
			+ "    o.organization_name AS organizationOrganizationName,\r\n"
			+ "    CB.created_on AS createdOn,\r\n"
			+ "	   CB.masked_aadhar AS MaskedAadhar,\r\n"
			+ "	   CB.aadhar_linked AS AadharLinked,\r\n"
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
			+ "    t_dgv_candidate_status CS\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_candidate_basic CB ON CS.candidate_id = CB.candidate_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_status_master SM ON SM.status_master_id = CS.status_master_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_user_master AS u ON u.user_id = CS.created_by\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_color_master AS c ON c.color_id = CS.color_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_organization_master o ON o.organization_id = u.orgainzation_id\r\n"
			+ "WHERE\r\n"
			+ "	CS.created_on between ?1 and ?2 and o.organization_id in (?3) and CB.created_by in (?4)", nativeQuery = true)
	List<CandidateStatusDto> findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdInAndCreatedByUserIdIn(
			Date startDate, Date endDate, List<Long> organizationIds, List<Long> agentIds);

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
			+ "    CB.experience_in_month AS experienceInMonth,\r\n"
			+ "    CB.itr_pan_number AS panNumber,\r\n"
			+ "    SM.status_name AS statusName,\r\n"
			+ "    CS.created_on AS statusDate,\r\n"
			+ "    u.user_first_name AS createdByUserFirstName,\r\n"
			+ "    u.user_last_name AS createdByUserLastName,\r\n"
			+ "    c.color_name AS colorName,\r\n"
			+ "    o.organization_name AS organizationOrganizationName,\r\n"
			+ "    CB.created_on AS createdOn,\r\n"
			+ "    CB.pan_name AS panName,\r\n"
			+ "    CB.pan_dob AS panDob,\r\n"
			+ "	   CB.masked_aadhar AS MaskedAadhar,\r\n"
			+ "	   CB.aadhar_linked AS AadharLinked,\r\n"
//			+ "    (SELECT \r\n"
//			+ "            I.id_holder_dob\r\n"
//			+ "        FROM\r\n"
//			+ "            t_dgv_candidate_id_items I\r\n"
//			+ "                LEFT JOIN\r\n"
//			+ "            t_dgv_source_service_master S ON I.service_source_master_id = S.source_service_id\r\n"
//			+ "        WHERE\r\n"
//			+ "            I.candidate_id = CB.candidate_id\r\n"
//			+ "                AND S.service_code = 'PAN'\r\n"
//			+ "        LIMIT 1) AS panDob,\r\n"
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
			+ "    t_dgv_candidate_status CS\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_candidate_basic CB ON CS.candidate_id = CB.candidate_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_status_master SM ON SM.status_master_id = CS.status_master_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_user_master AS u ON u.user_id = CS.created_by\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_color_master AS c ON c.color_id = CS.color_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_organization_master o ON o.organization_id = u.orgainzation_id\r\n"
			+ "WHERE\r\n"
			+ "	CB.created_by in (?1) and SM.status_code in (?2) AND CS.created_on BETWEEN ?3 AND ?4", nativeQuery = true)
	List<CandidateStatusDto> findAllByCreatedByUserIdInAndStatusMasterStatusCodeIn(List<Long> agentIds,
			List<String> statusList, Date fromDate, Date toDate);
//	List<CandidateStatusDto> findAllByCreatedByUserIdInAndStatusMasterStatusCodeIn(List<Long> agentIds,
//			List<String> statusList);

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
			+ "    CB.experience_in_month AS experienceInMonth,\r\n"
			+ "    CB.itr_pan_number AS panNumber,\r\n"
			+ "    SM.status_name AS statusName,\r\n"
			+ "    CS.created_on AS statusDate,\r\n"
			+ "    u.user_first_name AS createdByUserFirstName,\r\n"
			+ "    u.user_last_name AS createdByUserLastName,\r\n"
//			+ "    c.color_name AS colorName,\r\n"
			+ "    o.organization_name AS organizationOrganizationName,\r\n"
			+ "    CB.created_on AS createdOn,\r\n"
			+ "    CB.pan_name AS panName,\r\n"
			+ "    CB.pan_dob AS panDob,\r\n"
			+ "	   CB.masked_aadhar AS MaskedAadhar,\r\n"
			+ "	   CB.aadhar_linked AS AadharLinked,\r\n"
//			+ "    (SELECT \r\n"
//			+ "            I.id_holder_dob\r\n"
//			+ "        FROM\r\n"
//			+ "            t_dgv_candidate_id_items I\r\n"
//			+ "                LEFT JOIN\r\n"
//			+ "            t_dgv_source_service_master S ON I.service_source_master_id = S.source_service_id\r\n"
//			+ "        WHERE\r\n"
//			+ "            I.candidate_id = CB.candidate_id\r\n"
//			+ "                AND S.service_code = 'PAN'\r\n"
//			+ "        LIMIT 1) AS panDob,\r\n"
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
			+ "        LEFT JOIN\r\n"
//			+ "    t_dgv_color_master AS c ON c.color_id = CS.color_id\r\n"
//			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_organization_master o ON o.organization_id = u.orgainzation_id\r\n"
			+ "WHERE\r\n"
			+ "o.organization_id in (?1) and SM.status_code in (?2) AND CS.candidate_status_change_timestamp BETWEEN ?3 AND ?4 GROUP BY CB.candidate_id", nativeQuery = true)
	List<CandidateStatusDto> findAllByCandidateOrganizationOrganizationIdInAndStatusMasterStatusCodeIn(
			List<Long> organizationIds, List<String> statusList, Date fromDate, Date toDate);
//	List<CandidateStatusDto> findAllByCandidateOrganizationOrganizationIdInAndStatusMasterStatusCodeIn(
//			List<Long> organizationIds, List<String> statusList);
	
	// report delivered
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
			+ "    o.organization_name AS organizationOrganizationName,\r\n"
			+ "    CB.created_on AS createdOn,\r\n"
			+ "	   CB.masked_aadhar AS MaskedAadhar,\r\n"
			+ "	   CB.aadhar_linked AS AadharLinked,\r\n"
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
			+ "        LIMIT 1) AS candidateUanName,\r\n"
			+ "    (SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 7\r\n"
			+ "        LIMIT 1) AS qcCreatedOn,\r\n"
			+ "    (SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 13\r\n"
			+ "        LIMIT 1) AS interimCreatedOn\r\n"
			+ "FROM\r\n"
			+ "    t_dgv_candidate_status_history CS\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_candidate_basic CB ON CS.candidate_id = CB.candidate_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_status_master SM ON SM.status_master_id = CS.status_master_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_user_master AS u ON u.user_id = CS.created_by\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_organization_master o ON o.organization_id = u.orgainzation_id\r\n"
			+ "WHERE\r\n"
			+ "	CS.candidate_status_change_timestamp between ?1 and ?2 and o.organization_id in (?3) and CB.created_by in (?4) and SM.status_code In (?5) GROUP BY CB.candidate_id", nativeQuery = true)
	List<CandidateStatusDto> findReportDeliveredByUserIdAndStatus(
			Date startDate, Date endDate, List<Long> organizationIds, List<Long> agentIds, List<String> statusList);
	
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
			+ "    o.organization_name AS organizationOrganizationName,\r\n"
			+ "    CB.created_on AS createdOn,\r\n"
			+ "	   CB.masked_aadhar AS MaskedAadhar,\r\n"
			+ "	   CB.aadhar_linked AS AadharLinked,\r\n"
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
			+ "        LIMIT 1) AS candidateUanName,\r\n"
			+ "    (SELECT \r\n"
			+ "            csh.created_on\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 7\r\n"
			+ "        LIMIT 1) AS qcCreatedOn,\r\n"
			+ "    (SELECT \r\n"
			+ "            csh.created_on\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 13\r\n"
			+ "        LIMIT 1) AS interimCreatedOn,\r\n"
			+ "    (SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 7\r\n"
			+ "        LIMIT 1) AS qcCreatedOn,\r\n"
			+ "    (SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 13\r\n"
			+ "        LIMIT 1) AS interimCreatedOn\r\n"
			+ "FROM\r\n"
			+ "    t_dgv_candidate_status_history CS\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_candidate_basic CB ON CS.candidate_id = CB.candidate_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_status_master SM ON SM.status_master_id = CS.status_master_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_user_master AS u ON u.user_id = CS.created_by\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_organization_master o ON o.organization_id = u.orgainzation_id\r\n"
			+ "WHERE\r\n"
			+ "	CS.candidate_status_change_timestamp between ?1 and ?2 and o.organization_id in (?3) and SM.status_code In (?4) GROUP BY CB.candidate_id", nativeQuery = true)
	List<CandidateStatusDto> findReportDeliveredByOrganizationIdAndStatus(Date startDate,
			Date endDate, List<Long> organizationIds, List<String> statusList);

	@Query("select(candidateStatusId) FROM CandidateStatus  WHERE candidate.candidateId IN :candidateId")
	List<Long> getCandidateStatusIdByCandidateId(List<Long> candidateId);
	
	@Query(value = "SELECT \r\n"
			+ "    CB.applicant_id AS applicantId,\r\n"
			+ "    CB.candidate_name AS candidateName,\r\n"
			+ "    CB.date_of_birth AS dateOfBirth,\r\n"
			+ "    u.user_first_name AS createdByUserFirstName,\r\n"
			+ "    u.user_last_name AS createdByUserLastName,\r\n"
			+ "    o.organization_name AS organizationName,\r\n"
			+ "	(SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 1\r\n"
			+ "        LIMIT 1) AS uploadedDate,\r\n"
			+ "	(SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 3\r\n"
			+ "        LIMIT 1) AS inviteSentDate,\r\n"
			+ "    (SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 7\r\n"
			+ "        LIMIT 1) AS qcCreatedOn,\r\n"
			+ "    (SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 13\r\n"
			+ "        LIMIT 1) AS interimDate,\r\n"
			+ "    (SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 18\r\n"
			+ "        LIMIT 1) AS purgedDate,\r\n"
			+ "    (SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 12\r\n"
			+ "        LIMIT 1) AS processDeclinedDate,\r\n"
			+ "    (SELECT \r\n"
			+ "            DATE_FORMAT(csh.created_on, '%d-%m-%Y %H:%i')\r\n"
			+ "        FROM\r\n"
			+ "            t_dgv_candidate_status_history csh\r\n"
			+ "        WHERE\r\n"
			+ "            csh.candidate_id = CB.candidate_id\r\n"
			+ "                AND csh.status_master_id = 4\r\n"
			+ "        LIMIT 1) AS invitationExpiredDate\r\n"
			+ "FROM\r\n"
			+ "    t_dgv_candidate_status_history CS\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_candidate_basic CB ON CS.candidate_id = CB.candidate_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_status_master SM ON SM.status_master_id = CS.status_master_id\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_user_master AS u ON u.user_id = CB.created_by\r\n"
			+ "        LEFT JOIN\r\n"
			+ "    t_dgv_organization_master o ON o.organization_id = u.orgainzation_id\r\n"
			+ "WHERE\r\n"
			+ "	CS.candidate_status_change_timestamp between ?1 and ?2 and o.organization_id in (?3) and SM.status_code In (?4) GROUP BY CB.candidate_id HAVING purgedDate IS NOT NULL", nativeQuery = true)
	List<candidatePurgedReportDto> findPurgedCandidateByOrganizationIdAndStatus(Date startDate,
			Date endDate, List<Long> organizationIds, List<String> statusList);

			@Query("SELECT COUNT(DISTINCT csh.candidate.id) " +
		       "FROM CandidateStatus csh " +
		       "WHERE csh.candidate.organization.organizationId IN (:organizationId) " +
		       "AND csh.lastUpdatedOn BETWEEN :startDate AND :endDate " +
		       "AND csh.statusMaster.statusCode IN (:status)")
		Integer countDistinctCandidateStatus(@Param("organizationId") List<Long> organizationId,
		                                         @Param("startDate") Date startDate,
		                                         @Param("endDate") Date endDate,
		                                         @Param("status") List<String> status);
}
 