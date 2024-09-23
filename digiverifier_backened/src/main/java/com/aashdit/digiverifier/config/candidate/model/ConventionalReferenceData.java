/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Nambi
 */
@Data
@Entity
public class ConventionalReferenceData {

	@Id
	@Column(name = "conventional_reference_data_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long conventionalReferenceDataId;
	
	
	@NotNull
	@OneToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidateId;
	
	@Column(name = "date_of_joining")
	private String dateOfJoining;
	
	@Column(name = "fresher")
	private String fresher;
	
	@Column(name = "cea_initiation_date")
	private String ceaInitiationDate;
	
	@Column(name = "ce_insufficiency")
	private String ceInsufficiency;
	
	@Column(name = "re_initiation_date")
	private String reInitiationDate;
	
	@Column(name = "supplementary_date")
	private String supplementaryDate;
	
	@Column(name = "supplementary_report_sla")
	private String supplementaryReportSLA; 
	
	@Column(name = "date_of_birth")
	private String dateOfBirth;
}
