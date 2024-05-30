package com.aashdit.digiverifier.config.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.admin.model.VendorChecks;

import java.util.Date;
import java.util.List;
import com.aashdit.digiverifier.config.candidate.model.Candidate;


@Repository
public interface VendorChecksRepository extends JpaRepository<VendorChecks, Long> {

    VendorChecks findByVendorcheckId(Long VendorcheckId);
    List<VendorChecks> findAllByCandidateCandidateId(Long candidateId);
    
    @Query("FROM VendorChecks WHERE vendorId = :vendorId AND createdOn BETWEEN :fromDate AND :toDate AND vendorCheckStatusMaster.vendorCheckStatusMasterId = :vendorCheckStatusMasterId ORDER BY createdOn DESC")
    Page<VendorChecks> findAllByVendorId(@Param("vendorId") Long vendorId,@Param("fromDate") Date fromDate,@Param("toDate")  Date toDate,@Param("vendorCheckStatusMasterId") Long vendorCheckStatusMasterId, Pageable pageable);
    
    List<VendorChecks> findByCandidateCandidateIdAndSourceSourceId(Long candidateId,Long sourceId);
    VendorChecks findByCandidateCandidateIdAndVendorIdAndSourceSourceIdAndDocumentnameAndCheckType(Long candidateId,Long vendorId,Long sourceId,String documentname,String checkType);
    VendorChecks findByCandidateCandidateIdAndVendorIdAndSourceSourceIdAndCheckType(Long candidateId,Long vendorId,Long sourceId,String checkType);
    
    @Query(value = "FROM VendorChecks WHERE createdOn BETWEEN :startDate AND :endDate")
	List<VendorChecks> getByDateRange(@Param("startDate")Date startDate,@Param("endDate")Date endDate);

    @Query("select(vendorcheckId) FROM VendorChecks  WHERE candidate.candidateId IN :candidateId")
	List<Long> getvendorCheckIdByCandidateId(List<Long> candidateId);
    
    @Query(value = "SELECT * FROM t_dgv_vendor_checks " +
            "WHERE vendor_id = :vendorId " +
            "AND (candidate_name = :userSearchInput OR candidate_id = :userSearchInput)", nativeQuery = true)
    List<VendorChecks> searchAllVendorChecksByVendorIdAndUserSearchInput(
        @Param("vendorId") Long vendorId, 
        @Param("userSearchInput") String userSearchInput);


    @Query(value = "SELECT * FROM t_dgv_vendor_checks " +
            "WHERE vendor_id = :vendorId " +
            "AND (candidate_id IN :userSearchInput OR created_by = :agentId OR source_id = :sourceId OR vendor_checkstatus_master_id = :checkStatusId)", nativeQuery = true)
    List<VendorChecks> searchAllVendorChecksByVendorIdAndUserSearchInputByCandidateName(
        @Param("vendorId") Long vendorId,
        @Param("userSearchInput") List<Long> userSearchInput,
        @Param("agentId") Long agentId,
        @Param("sourceId") Long sourceId,
        @Param("checkStatusId") Long checkStatusId);
    
    @Query("FROM VendorChecks WHERE vendorId = :vendorId AND createdOn BETWEEN :fromDate AND :toDate")
    List<VendorChecks> vendorDashboardStatusAndCount(@Param("vendorId") Long vendorId,@Param("fromDate") Date fromDate,@Param("toDate")  Date toDate);
   
    @Query("FROM VendorChecks WHERE vendorcheckId = :vendorCheckId AND candidate.candidateId = :candidateId")
    VendorChecks findByCandidateCandidateIdAndvendorcheckId(@Param("candidateId") Long candidateId, @Param("vendorCheckId") Long vendorCheckId);

    
}
