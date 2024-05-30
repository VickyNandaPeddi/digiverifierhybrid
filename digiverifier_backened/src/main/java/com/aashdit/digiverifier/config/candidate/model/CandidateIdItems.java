package com.aashdit.digiverifier.config.candidate.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import com.aashdit.digiverifier.config.superadmin.model.Color;
import com.aashdit.digiverifier.config.superadmin.model.ServiceSourceMaster;

import lombok.Data;
@Data
@Entity
@Table(name="t_dgv_candidate_id_items")
public class CandidateIdItems implements Serializable {
	
	private static final long serialVersionUID = -2084642723714482L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "items_id")
	private Long candidateItemsId;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;

	@ManyToOne
	@JoinColumn(name = "color_id")
	private Color color;
	
	@ManyToOne
	@JoinColumn(name = "service_source_master_id")
	private ServiceSourceMaster serviceSourceMaster;
	
	@Column(name = "created_on")
	private Date createdOn;
	
	@Column(name = "id_number")
	private String idNumber;
	
	@Column(name = "id_holder")
	private String idHolder;

	@Column(name = "id_holder_dob")
	private String idHolderDob;

}
