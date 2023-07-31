package com.aashdit.digiverifier.config.superadmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aashdit.digiverifier.config.superadmin.model.VendorCheckStatusMaster;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;

public interface VendorCheckStatusMasterRepository extends JpaRepository<VendorCheckStatusMaster, Long> {

	VendorCheckStatusMaster findByCheckStatusCode(String checkStatusCode);
	VendorCheckStatusMaster findByVendorCheckStatusMasterId(String vendorCheckStatusMasterId);
	VendorCheckStatusMaster findByVendorCheckStatusMasterId(Long vendorCheckStatusMasterId);

}
