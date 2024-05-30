package com.aashdit.digiverifier.config.superadmin.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.model.VendorUploadChecks;

import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name="t_dgv_vendor_checkstatus_master")
public class VendorCheckStatusMaster implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7858395421908518082L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vendor_checkstatus_master_id")
	private Long vendorCheckStatusMasterId;
	
	@Column(name = "checkstatus_name")
	private String checkStatusName;
	
	@Column(name = "checkstatus_code")
	private String checkStatusCode;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "vendor_check_status")
	private Boolean VendorCheckStatus;


}
