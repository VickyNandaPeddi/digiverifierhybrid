package com.aashdit.digiverifier.config.candidate.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="t_dgv_status_master")
public class StatusMaster implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7858395421908518082L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_master_id")
	private Long statusMasterId;
	
	@Column(name = "status_name")
	private String statusName;
	
	@Column(name = "status_code")
	private String statusCode;

}
