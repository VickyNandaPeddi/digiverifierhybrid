package com.aashdit.digiverifier.config.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.model.VendorUploadChecks;
import java.util.List;
import com.aashdit.digiverifier.config.candidate.model.Candidate;

import jakarta.transaction.Transactional;


@Repository
public interface VendorUploadChecksRepository extends JpaRepository<VendorUploadChecks, Long> {

    // VendorChecks findByCandidate(Candidate candidate);
  VendorUploadChecks findByVendorChecksVendorcheckId(Long VendorcheckId);
  
  @Transactional
  @Modifying                  
  @Query(value = "DELETE FROM VendorUploadChecks WHERE vendorChecks.vendorcheckId IN :vendorcheckIds")
	void deleteVendorCheckUploadByVendorCheckIds(List<Long> vendorcheckIds);
  
  @Query("SELECT vm FROM VendorUploadChecks vm WHERE vm.vendorChecks.vendorcheckId IN :vendorCheckIds")
  List<VendorUploadChecks> findByVendorChecksVendorcheckIds(@Param("vendorCheckIds") List<Long> vendorCheckIds);
}
