package com.aashdit.digiverifier.config.admin.repository;

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
    List<VendorChecks> findAllByVendorId(Long vendorId);
    List<VendorChecks> findByCandidateCandidateIdAndSourceSourceId(Long candidateId,Long sourceId);
    VendorChecks findByCandidateCandidateIdAndVendorIdAndSourceSourceIdAndDocumentname(Long candidateId,Long vendorId,Long sourceId,String documentname);
    VendorChecks findByCandidateCandidateIdAndVendorIdAndSourceSourceId(Long candidateId,Long vendorId,Long sourceId);
    
    @Query(value = "FROM VendorChecks WHERE createdOn BETWEEN :startDate AND :endDate")
	List<VendorChecks> getByDateRange(@Param("startDate")Date startDate,@Param("endDate")Date endDate);

}
