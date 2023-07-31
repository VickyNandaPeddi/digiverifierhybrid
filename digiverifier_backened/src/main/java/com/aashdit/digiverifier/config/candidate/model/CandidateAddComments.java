package com.aashdit.digiverifier.config.candidate.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.aashdit.digiverifier.config.admin.model.User;

import lombok.Data;

@Data
@Entity
@Table(name="t_dgv_candidate_caf_addcomments")
public class CandidateAddComments implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4386254957126811179L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "candidate_addcommen_id")
	private Long candidateAddcommentId;
	
	@ManyToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;
	
	@Lob
	@Column(name = "Comments", length=2000)
	private String comments;

	@Type(type="org.hibernate.type.BinaryType")
    @Column(name = "attachments_documents", columnDefinition="LONGBLOB")
    private byte[] attachments;
	
	@ManyToOne
	@JoinColumn(name = "created_by")
	private User createdBy;
	
	@Column(name = "created_on")
	private Date createdOn;


	
}
