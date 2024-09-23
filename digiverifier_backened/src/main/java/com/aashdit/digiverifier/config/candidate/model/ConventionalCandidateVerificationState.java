/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.model;

import java.time.ZonedDateTime;

import com.aashdit.digiverifier.config.superadmin.model.Color;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Nambi
 */
@Data
@Entity
@Table(name = "t_dgv_conventional_candidate_verification_state")
public class ConventionalCandidateVerificationState {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "candidate_verification_state_id")
	private Long candidateVerificationStateId;
	
	@NotNull
	@OneToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;
	
	@Column(name = "case_initiation_time")
	private ZonedDateTime caseInitiationTime;
	
	@Column(name = "interim_report_time")
	private ZonedDateTime interimReportTime;
	
	@Column(name = "final_report_time")
	private ZonedDateTime finalReportTime;
	
	@Column(name = "sr_report_time")
	private ZonedDateTime srReportTime;
	
	@Column(name = "pre_approval_report_time")
	private ZonedDateTime preApprovalTime;
	
	
	@ManyToOne
	@JoinColumn(name = "pre_approval_color_code_status")
	private Color preApprovalColorCodeStatus;
	
	@ManyToOne
	@JoinColumn(name = "interim_color_code_status")
	private Color interimColorCodeStatus;
	
	@ManyToOne
	@JoinColumn(name = "final_color_code_status")
	private Color finalColorCodeStatus;
	
	@Column(name = "case_re_initiation_time")
	private ZonedDateTime caseReInitiationTime;
	
	@Column(name = "interim_report_amended_time")
	private ZonedDateTime interimReportAmendedTime;
	
	@Column(name = "supplementary_report_time")
	private ZonedDateTime supplementaryReport;


}
