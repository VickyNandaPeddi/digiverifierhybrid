package com.aashdit.digiverifier.config.candidate.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="t_dgv_suspect_clg_master")
public class SuspectClgMaster implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -950433494770697580L;
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "suspect_clg_master_id")
	private Long suspectClgMasterId;
	
	@Column(name = "suspect_institution_name")
	private String suspectInstitutionName;
	
	@Column(name = "associated_institution")
	private String associatedInstitution;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "classified_as")
	private String classifiedAs;
	
	@Column(name = "date_modified")
	private String dateModified;
	
	@Column(name = "vendor")
	private String vendor;
	
	@Column(name = "is_active")
	private Boolean isActive;

}
