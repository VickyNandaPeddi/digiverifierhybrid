package com.aashdit.digiverifier.config.admin.model;

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
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import com.aashdit.digiverifier.config.superadmin.model.Organization;

import lombok.Data;

@Data
@Entity
@Table(name = "t_dgv_agent_sample_csv_xls_master")
public class AgentSampleCsvXlsMaster implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3891677023404920940L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "agent_sample_id")
	private Long agentSampleId;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;
	
	//@Type(type="org.hibernate.type.BinaryType")
	@JdbcTypeCode(Types.BINARY)
    @Column(name = "agent_sample_csv", columnDefinition="BLOB")
    private byte[] agentSampleCsv;
	
	//@Type(type="org.hibernate.type.BinaryType")
	@JdbcTypeCode(Types.BINARY)
    @Column(name = "agent_sample_xls", columnDefinition="BLOB")
    private byte[] agentSampleXls;
	
	@Column(name = "uploaded_timestamp")
	private Date uploadedTimestamp;
	
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
