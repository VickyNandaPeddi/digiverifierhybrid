package com.aashdit.digiverifier.config.admin.repository;

import com.aashdit.digiverifier.config.admin.dto.VendorChecksDto;
import com.aashdit.digiverifier.vendorcheck.dto.ReportUtilizationVendorDto;
import com.aashdit.digiverifier.vendorcheck.model.ConventionalVendorCandidatesSubmitted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.admin.model.VendorChecks;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.aashdit.digiverifier.config.candidate.model.Candidate;


@Repository
public interface VendorChecksRepository extends JpaRepository<VendorChecks, Long> {

    VendorChecks findByVendorcheckId(Long VendorcheckId);

    VendorChecks findBySourceSourceId(Long sourceId);

    List<VendorChecks> findAllByCandidateCandidateId(Long candidateId);

    @Query(value = "select da.* from t_dgv_vendor_checks da where candidate_id=?1 and source_id=?2  group by source_id", nativeQuery = true)
    VendorChecks findByCandidateIdAndSourceID(Long candidateId, Long sourceId);

    List<VendorChecks> findAllByVendorId(Long vendorId);

    //VendorChecks findby
    @Query("select vc from VendorChecks vc where vc.vendorId=?1 group by vc.source.sourceId")
    List<VendorChecks> findAllSourceByVendorId(Long vendorId);

    @Query("select vc from VendorChecks vc,VendorUploadChecks  vuc where vc.vendorcheckId in vuc.vendorChecks.vendorcheckId")
    List<VendorChecks> findAllVendorChecksInVendorUploadChecks();

    @Query("select vc from VendorChecks vc,VendorUploadChecks  vuc where vc.vendorcheckId in vuc.vendorChecks.vendorcheckId  and vc.vendorId=?1 group by vc.vendorcheckId")
    List<VendorChecks> findAllGroupByVendorCheckId(Long vendorId);

    VendorChecks findByCandidateCandidateIdAndSourceSourceId(Long candidateId, Long sourceId);

    @Query("select vc from VendorChecks  vc,VendorUploadChecks  vuc where vc.vendorcheckId in vuc.vendorChecks.vendorcheckId")
    List<VendorChecks> findAllVendorCheckInVenodorUploadChecks();

    //    @Query(value = "SELECT V.vendor_id as vendorId , C.candidate_id as candidateId, (GROUP_CONCAT(distinct S.source_id)) AS sourceId FROM t_dgv_vendor_checks V  JOIN t_dgv_vendor_checks C ON C.vendor_id = V.vendor_id     JOIN t_dgv_vendor_checks S ON S.vendor_id = V.vendor_id AND S.candidate_id = C.candidate_id GROUP BY V.vendor_id, C.candidate_id", nativeQuery = true)
    @Query(value = "SELECT V.vendor_id as vendorId , C.candidate_id as candidateId, (GROUP_CONCAT(distinct S.source_id)) AS sourceId FROM t_dgv_vendor_checks V  JOIN t_dgv_vendor_checks C ON C.vendor_id = V.vendor_id     JOIN t_dgv_vendor_checks S ON S.vendor_id = V.vendor_id AND S.candidate_id = C.candidate_id where S.source_id = ?1 GROUP BY V.vendor_id, C.candidate_id", nativeQuery = true)
    List<ReportUtilizationVendorDto> findAllVendorCandidateAndSourceId(Long sourceId);

    @Query(value = "select vc.* from t_dgv_vendor_checks vc where   vc.vendor_id = ?1  and vc.candidate_id = ?2 and vc.source_id = ?3", nativeQuery = true)
    List<VendorChecks> findByCandidateIdANdVendorIdAndCandidateId(Long vendorId, Long candidateId, Long sourceId);


    VendorChecks findByVendorIdAndCandidateCandidateIdAndSourceSourceId(Long vendorId, Long candidateId, Long sourceId);

    @Query(value = "select  * from t_dgv_vendor_checks  v where v.candidate_id=?1 and v.vendor_id=?2 and v.source_id=?3 and v.document_name=?4 and v.licheckid=?5 and v.created_at= (SELECT MAX(created_at) FROM t_dgv_vendor_checks)", nativeQuery = true)
    VendorChecks findByCandidateCandidateIdAndVendorIdAndSourceSourceIdAndDocumentnameAndLicheckId(Long candidateId, Long vendorId, Long sourceId, String documentname, Long licheckId);

    VendorChecks findByCandidateCandidateIdAndVendorIdAndSourceSourceId(Long candidateId, Long vendorId, Long sourceId);

    VendorChecks findByCandidateConventionalRequestIdAndVendorIdAndSourceSourceId(Long requestId, Long vendorId, Long sourceId);

    @Query("FROM VendorChecks WHERE vendorId =:vendorId and createdOn between :startDate and :endDate")
    List<VendorChecks> findAllByDateRange(@Param("vendorId") Long vendorId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT vc.* FROM t_dgv_vendor_checks vc inner join t_dgv_conventional_vendorchecks_to_perform cl  WHERE vc.vendor_id = :userId AND vc.licheckId = cl.Id AND (cl.check_unique_id LIKE %:userSearchInput% OR vc.document_name LIKE %:userSearchInput% )AND vc.created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<VendorChecks> searchAllVendorCheckFilter(
            @Param("userId") Long userId,
            @Param("userSearchInput") String userSearchInput,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT vc.* FROM t_dgv_vendor_checks vc inner join t_dgv_conventional_vendorchecks_to_perform cl  WHERE vc.vendor_id = :userId AND vc.licheckId = cl.Id AND (vc.source_id LIKE %:userSearchInput% )AND vc.created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<VendorChecks> searchAllVendorCheckFilterForSource(
            @Param("userId") Long userId,
            @Param("userSearchInput") String userSearchInput,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);


    @Query(value = "SELECT vc.* FROM t_dgv_vendor_checks vc inner join t_dgv_conventional_vendorchecks_to_perform cl  WHERE vc.vendor_id = :userId AND vc.licheckId = cl.Id AND (cl.mode_of_verification_performed LIKE %:userSearchInput% )AND vc.created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<VendorChecks> searchAllVendorCheckFilterForModeOfVerification(
            @Param("userId") Long userId,
            @Param("userSearchInput") String userSearchInput,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT vc.* FROM t_dgv_vendor_checks vc inner join t_dgv_conventional_vendorchecks_to_perform cl  WHERE vc.vendor_id = :userId AND vc.licheckId = cl.Id AND (cl.check_status LIKE %:userSearchInput% )AND vc.created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<VendorChecks> searchAllVendorCheckFilterForCheckStatus(
            @Param("userId") Long userId,
            @Param("userSearchInput") String userSearchInput,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT vc.* FROM t_dgv_vendor_checks vc inner join t_dgv_conventional_vendorchecks_to_perform cl  WHERE vc.vendor_id = :userId AND vc.licheckId = cl.Id AND (vc.candidate_id  LIKE %:userSearchInput% )AND vc.created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<VendorChecks> searchAllVendorCheckFilterByCandidateId(
            @Param("userId") Long userId,
            @Param("userSearchInput") String userSearchInput,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

}
