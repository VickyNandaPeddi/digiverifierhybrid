/**
 * 
 */
package com.aashdit.digiverifier.config.admin.model;

import java.util.ArrayList;
import java.util.Date;

import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.superadmin.model.Source;
import com.aashdit.digiverifier.config.superadmin.model.VendorCheckStatusMaster;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Nambi
 */
@Data
@Entity
@Table(name = "t_dgv_vendor_checks_status_history")
public class VendorCheckStatusHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vendorCheckStatusHistory_Id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;
	
	@Column(name = "check_id")
	private Long checkId;
	
	@Column(name = "check_status")
	private String checkStatus;
	
	@Column(name = "check_name")
	private String checkName;
	
	@Column(name = "created_on")
	private Date createdOn;
	
	@Column(name = "created_by")
    private Long createdBy;
	
	@Column(name = "candidate_status")
	private String candidateStatus;

}
