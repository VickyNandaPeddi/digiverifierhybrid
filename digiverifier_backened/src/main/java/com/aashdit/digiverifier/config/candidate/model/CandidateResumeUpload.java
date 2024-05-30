package com.aashdit.digiverifier.config.candidate.model;

import java.io.Serializable;
import java.sql.Types;
import java.util.Date;

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

import com.aashdit.digiverifier.common.model.Content;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import com.aashdit.digiverifier.config.admin.model.User;

import lombok.Data;

@Data
@Entity
@Table(name="t_dgv_candidate_resume_upload")
public class CandidateResumeUpload implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7795148256100383688L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "candidate_resume_upload_id")
	private Long candidateResumeUploadId;
	
	//@Type(type="org.hibernate.type.BinaryType")
	@JdbcTypeCode(Types.BINARY)
    @Column(name = "candidate_resume", columnDefinition="BLOB")
    private byte[] candidateResume;
	
	@OneToOne
	@JoinColumn(name = "content_id",insertable = false,updatable = false)
	private Content content;
	
	@Column(name = "content_id")
	private Long contentId;
	
	@NotNull
	@OneToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;
	
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
