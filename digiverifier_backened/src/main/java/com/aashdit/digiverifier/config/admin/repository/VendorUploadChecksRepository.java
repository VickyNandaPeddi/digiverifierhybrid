package com.aashdit.digiverifier.config.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.model.VendorUploadChecks;
import java.util.List;
import com.aashdit.digiverifier.config.candidate.model.Candidate;


@Repository
public interface VendorUploadChecksRepository extends JpaRepository<VendorUploadChecks, Long> {

    // VendorChecks findByCandidate(Candidate candidate);
  VendorUploadChecks findByVendorChecksVendorcheckId(Long VendorcheckId);
}
