package com.aashdit.digiverifier.config.admin.dto;

import java.util.ArrayList;
import java.util.Date;

import com.aashdit.digiverifier.config.superadmin.dto.CheckAttributeAndValueDTO;
import com.aashdit.digiverifier.config.superadmin.model.Source;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorUploadChecksDto {

	private String userFirstName;
	private Long VendorChecks;
	private byte[] document;
	private String documentname;
	private String AgentColor;
	private String colorHexCode;
	private Long colorId;
	private ArrayList<CheckAttributeAndValueDTO> vendorAttirbuteValue;
	private Date createdOn;
	private Source source;
	private ArrayList<String> vendorAttribute;
	private String checkType;
	private String documentPathkey;
	private String checkStatus;
	private Boolean conventionalQcPending;


	
	
	
   
}
