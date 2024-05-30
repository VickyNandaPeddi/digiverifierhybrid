package com.aashdit.digiverifier.config.candidate.model;

import lombok.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import com.aashdit.digiverifier.config.superadmin.model.Color;

@Data
@Entity
@Table(name = "t_dgv_candidate_verification_state")
public class CandidateVerificationState {
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
	
}
