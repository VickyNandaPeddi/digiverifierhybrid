/**
 * 
 */
package com.aashdit.digiverifier.config.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aashdit.digiverifier.config.admin.model.VendorCheckStatusHistory;

/**
 * Nambi
 */
public interface VendorCheckStatusHistoryRepository extends JpaRepository<VendorCheckStatusHistory, Long> {

}
