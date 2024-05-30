/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.model;

import java.io.Serializable;
import java.util.Date;

import com.aashdit.digiverifier.config.admin.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Nambi
 */
@Data
@Entity
@Table(name="t_dgv_conventional_candidate_email_status")
public class ConventionalCandidateEmailStatus implements Serializable {
	
private static final long serialVersionUID = 741832079761336502L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "conventional_candidate_email_status_id")
	private Long conventionalCandidatEmailStatusId;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;
	
	@Column(name = "date_of_email_invite")
	private Date dateOfEmailInvite;
	
	@Column(name = "date_of_email_failure")
	private Date dateOfEmailFailure;
	
	@Column(name = "date_of_email_re_invite")
	private Date dateOfEmailReInvite;
	
	@Column(name = "date_of_email_expire")
	private Date dateOfEmailExpire;
	
	@ManyToOne
	@JoinColumn(name = "conventional_candidate_status_id")
	private ConventionalCandidateStatus conventionalCandidateStatus;
	
	@ManyToOne
	@JoinColumn(name = "created_by")
	private User createdBy;
	
	@Column(name = "created_on")
	private Date createdOn;
	
	@ManyToOne
	@JoinColumn(name = "last_updated_by")
	private User lastUpdatedBy;

	@Column(name = "last_updated_on")
	private Date lastUpdatedOn;

}
