package com.aashdit.digiverifier.config.superadmin.model;

import java.io.Serializable;
import java.util.Date;

import com.aashdit.digiverifier.config.admin.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "t_dgv_organization_config")
public class OrganizationConfig implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organization_config_id")
	private Long organizationConfigId;
	 
	private Long organizationId;
	
	private String configCode;
	
	private Integer purgeDay;
	
	private Integer auditTrailConfigValue;

	//enum('DAYS','MONTHS')
	private String auditTrailConfigQualifier;
	
	private Integer anonymousDataPurge; 
	
	private Integer customerAddrReportFlag; 
	
	private Integer candidateReportGenerationDays;
	
	private Integer isEmailDeliveryEnabled;

	private Integer inviteExpiryDays;
	
	// enum('YEAR','EMPLOYER','HIGHER_TENURE')
	private String candidateReportCond;
	
	private Integer candidateReportCondYears;
	
	private Integer candidateReportCondEmployers;

	private Integer tenureTolerance;
	
	//enum('NO_REPLY','CUSTOMER','USER')
	private String candidateFromEmailId;
	
	private String customer_mail_id;
	
	//enum('CUSTOMER','COMPANY') 
	private String employerConfiguration;
	
	//enum('OLD','NEW') 
	private String candidateUploadFlow;
	
	private String candidateFlowLink;
	
	//enum('DEFAULT','INPUT_EMPLOYERS','OUTPUT_EMPLOYERS') 
	private String pricePerEmployer;
	
	private Integer employerMatchPercent;
	
	private Integer sendInviteWhatsappSms;

	@ToString.Exclude 
	@ManyToOne
	@JoinColumn(name = "created_by")
	private User createdBy;
	
	@Column(name = "created_on")
	private Date createdOn;
	
	@ToString.Exclude
	@JsonBackReference(value="last-updated-by")
	@ManyToOne
	@JoinColumn(name = "last_updated_by")
	private User lastUpdatedBy;

	@Column(name = "last_updated_on")
	private Date lastUpdatedOn;
}
