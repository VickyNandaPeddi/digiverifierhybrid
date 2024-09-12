package com.aashdit.digiverifier.config.superadmin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.aashdit.digiverifier.config.admin.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "t_dgv_organization_email_template")
public class OrganizationEmailTemplate implements Serializable{
	private static final long serialVersionUID = -3790328078476964299L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "email_template_id")
	private Long emailTempId;
	
	@Column(name = "organization_id")
	private Long organizationId;
	
	@Column(name = "candidate_invite_email_subject")
	private String candidateInviteEmailSub;
	
	
	@Column(length = 2500 ,name = "candidate_invite_email_template")
	private String candidateInviteEmailTemp;
	
	@Column(name = "candidate_loa_email_subject")
	private String candidateLoaEmailSub;
	
	
	@Column(length = 2500 ,name = "candidate_loa_accepted_email_template")
	private String candidateLoaEmailTemp;
	
	
	@Column(length = 2500 ,name = "candidate_success_email_template")
	private String candidateSuccessEmailTemp;
	
	@Column(name = "updated_date")
	private Date updatedDate;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "cwf_copyright")
	private String cwfCopyRight;
	
    @Column(name = "cwf_logo", columnDefinition = "BLOB")
    private String cwfLogo;

}
