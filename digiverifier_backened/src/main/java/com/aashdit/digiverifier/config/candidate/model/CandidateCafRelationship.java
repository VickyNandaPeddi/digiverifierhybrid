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
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import com.aashdit.digiverifier.config.admin.model.User;

import lombok.Data;


@Data
@Entity
@Table(name="t_dgv_candidate_caf_relationship")
public class CandidateCafRelationship implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6629477214388045075L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "candidate_relationship_id")
	private Long candidateRelationshipId;
	
	@ManyToOne
	@JoinColumn(name = "candidate_status_id")
	private CandidateStatus candidateStatus;
	
	@Column(name = "candidate_relationship")
	private String candidateRelationship;
	
	
	//@Type(type="org.hibernate.type.BinaryType")
	@JdbcTypeCode(Types.BINARY)
    @Column(name = "document_uploaded", columnDefinition="BLOB")
    private byte[] documentUploaded;
	
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
	
	@Column(name = "rent_type")
	private String rentType;

}
