package com.aashdit.digiverifier.config.candidate.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import jakarta.validation.constraints.NotNull;

import com.aashdit.digiverifier.config.superadmin.model.Organization;

import lombok.Data;

@Data
@Entity
@Table(name="t_dgv_suspect_emp_master")
public class SuspectEmpMaster implements Serializable {

	//updated
//	public SuspectEmpMaster(String string, String string2) {
//		// TODO Auto-generated constructor stub
//	}

	public SuspectEmpMaster() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6150458727931674041L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "suspect_emp_master_id")
	private Long suspectEmpMasterId;
	
	@Column(name = "suspect_company_name")
	private String suspectCompanyName;
	
	@Column(name = "address")
	private String address;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;

	
	@Column(name = "location")
	private String location;
	
	@Column(name = "catagory")
	private String catagory;
	
	@Column(name = "approval_date")
	private String approvalDate;
	
	@Column(name = "vendor")
	private String vendor;
	
	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "created_on")
	private Date createdOn;


	 public SuspectEmpMaster(String suspectCompanyName, String address ) {
	 	super();
	 	this.suspectCompanyName = suspectCompanyName;
	 	this.address = address;
//	 	this.isActive = isActive;
		
	 }

}
