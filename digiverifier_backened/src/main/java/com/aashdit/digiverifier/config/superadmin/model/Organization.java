package com.aashdit.digiverifier.config.superadmin.model;

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
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import com.aashdit.digiverifier.config.admin.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "t_dgv_organization_master")
public class Organization implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3790328078476964299L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "organization_id")
	private Long organizationId;
	
	@NotBlank
	@Column(name = "organization_name")
	private String organizationName;
	
	@NotBlank
	@Column(name = "organization_email_id")
	private String organizationEmailId;
	
	@Column(name = "organization_location")
	private String organizationLocation;
	
	@Column(name = "customer_name")
	private String customerName;
	
	@Column(name = "gst_number")
	private String gstNumber;

	@Column(name = "pan_number")
	private String panNumber;

	@Column(name = "saac_code")
	private String saacCode;
	
	@Column(name = "shipment_address")
	private String shipmentAddress;
	
	@Column(name = "poc_name")
	private String pocName;
	
	@Column(name = "customer_phone_number")
	private String customerPhoneNumber;
	
	@Column(name = "organization_website")
	private String organizationWebsite;
	
	//@Type(type="org.hibernate.type.BinaryType")
	@JdbcTypeCode(Types.BINARY)
    @Column(name = "organization_logo", columnDefinition="BLOB")
    private byte[] organizationLogo;
	
	@Column(name = "billing_address")
	private String billingAddress;
	
	@Column(name = "accounts_poc")
	private String accountsPoc;
	
	@Column(name = "accounts_poc_phone_number")
	private String accountsPocPhoneNumber;
	
	@Column(name = "account_poc_email")
	private String accountPocEmail;

	@Column(name = "email_template_selection")
	private String emailTemplate;

	@Column(name = "email_config")
	private String emailConfig;

	@Column(name = "days_to_purge")
	private String daysToPurge;

	@Column(name = "report_backup_email")
	private String reportBackupEmail;

	@Column(name = "no_years_to_be_verified")
	private String noYearsToBeVerified;

	@Column(name = "show_validation")
	private Boolean showValidation;
	
	@Column(name = "total")
	private Double total;
	
	@Column(name = "is_active")
	private Boolean isActive;
	
	@Column(name = "updated_timestamp")
	private Date updatedTimestamp;
	
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
	
	@Column(name = "logo_url")
	private String logoUrl;
	
	@Column(name = "call_back_url")
	private String callBackUrl;
	
	@Column(name = "password_policy")
	private Boolean passwordPolicy;
	
	@Column(name = "last_passwords")
	private String lastPasswords;
}
