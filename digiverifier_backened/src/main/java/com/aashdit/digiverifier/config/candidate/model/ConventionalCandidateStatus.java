/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.model;

import java.io.Serializable;
import java.util.Date;

import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.superadmin.model.ServiceSourceMaster;

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
@Table(name="t_dgv_conventional_candidate_status")
public class ConventionalCandidateStatus implements Serializable {

	private static final long serialVersionUID = 8358339938720997566L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "conventional_candidate_status_id")
	private Long candidateStatusId;


	@NotNull
	@OneToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;

	@ManyToOne
	@JoinColumn(name = "source_service_id")
	private ServiceSourceMaster serviceSourceMaster;

	@OneToOne
	@JoinColumn(name = "status_master_id")
	private StatusMaster statusMaster;

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

	//	@OneToOne
	//	@JoinColumn(name = "conventional_status_id")
	//	private StatusMaster conventionalStatusId;


}
