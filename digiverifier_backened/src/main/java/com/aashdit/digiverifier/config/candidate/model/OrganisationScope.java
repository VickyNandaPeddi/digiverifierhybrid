package com.aashdit.digiverifier.config.candidate.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.transaction.Transactional;

import lombok.Data;

@Data
@Table(name = "t_dgv_organisation_scope")
@Entity
public class OrganisationScope {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "org_scope_id")
	private Long orgScopeId;  
	
	@Column(name = "candidate_id")
	private Long candidateId;
	
	@Column(name = "dual_employment", length=2000)
	private String dualEmployment;
	
	@Column(name = "dual_employment_color_id")
	private Long dualEmploymentColorId;

	@Column(name = "undisclosed", length=2000)
	private String undisclosed;
	
	@Column(name = "undisclosed_color_id")
	private Long undisclosedColorId;
	
	@Column(name = "data_not_found", length=2000)
	private String dataNotFound;
	
	@Column(name = "data_not_found_color_id")
	private Long dataNotFoundColorId;
	
	@Column(name = "dnh_db", length=2000)
	private String dNHDB;
	
	@Column(name = "dnh_db_color_id")
	private Long dNHDBColorId;
	
	@Column(name = "tenure_mismatch", length=2000)
	private String tenureMismatch;
	
	@Column(name = "tenure_mismatch_color_id")
	private Long tenureMismatchColorId;
	
	@Column(name = "overseas_employment", length=2000)
	private String overseasEmployment;
	
	@Column(name = "overseas_mployment_color_id")
	private Long overseasEmploymentColorId;

	@Column(name = "others", length=2000)
	private String others;
	
	@Column(name = "others_color_id")
	private Long othersColorId; 
}
