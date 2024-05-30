package com.aashdit.digiverifier.config.superadmin.model;

import java.io.Serializable;

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

@Data
@Entity
@Table(name = "t_dgv_source_service_master")
public class ServiceSourceMaster implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1760822105264530176L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "source_service_id")
	private Long sourceServiceId;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "source_id")
	private Source source;
	
	@Column(name = "service_name")
	private String serviceName;
	
	@Column(name = "service_api")
	private String serviceApi;
	
	@Column(name = "service_code")
	private String serviceCode;

}
