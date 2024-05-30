package com.aashdit.digiverifier.config.admin.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class vendorChecksDto {
	
	private String vendor_check_id;
	
	private byte[] candidate_id;
	
	private byte[] created_by;
	
	private String created_at;
	
	private String email_id;
	
	private Boolean expires_on;
	
	private Boolean tat;

    private String vendor_id;
	
	private String source_id;
	
	private String Is_proof_uploaded;
	
	private byte[] agent_Uploaded_Document;
	
	private String address;
		
	private String alternate_contact_no;
	
	private String candidate_name;
	
	private String contact_no;
	
	private String date_of_birth;
		
	private String document_name;
	
	private String father_name;
	
	private String type_of_panel;
	
	private String vendor_checkstatus_master_id;
		
	

}
