package com.aashdit.digiverifier.config.candidate.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import javax.validation.constraints.NotNull;

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
