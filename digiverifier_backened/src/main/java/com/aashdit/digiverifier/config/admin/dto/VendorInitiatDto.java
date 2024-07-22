package com.aashdit.digiverifier.config.admin.dto;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;

import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.superadmin.model.Source;
import com.aashdit.digiverifier.config.superadmin.model.VendorCheckStatusMaster;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorInitiatDto {

	
	private Long vendorId;
	
	private Long sourceId;

	private Long candidateId;

	private String documentname;

	private String candidateName;

	private String dateOfBirth;

	private String contactNo;

	private String fatherName;

	private String address;

	private String alternateContactNo;

	private String typeOfPanel;

	private Long vendorCheckStatusMasterId;
	
	private String value;
	
	/// NEW VALUES
	
	private Long vendorcheckId;
	
	private Candidate candidate;
	
//	private Long vendorId;
	
	private Source source;
	
    private Double tat;

	private String emailId;

	private Boolean expireson;

    private byte[] agentUploadedDocument;
	
    private User createdBy;

    private Date createdOn;

	private Boolean Isproofuploaded;

//	private String documentname;
//
//	private String candidateName;
//
//	private String dateOfBirth;
//
//	private String contactNo;
//
//	private String fatherName;
//
//	private String address;
//
//	private String alternateContactNo;
//
//	private String typeOfPanel;

	private VendorCheckStatusMaster vendorCheckStatusMaster;
	
    private ArrayList<String> venderAttirbuteValue;
	
    private ArrayList<String> agentAttirbuteValue;
	
	private Boolean stopCheck;
	
	private Date stopCheckCreatedOn;
	
	private User vendorIds;
	
	private String checkType;
	
	private String type;
	
	private String details;
	
	private String idItemsDetails;
	
	private String AgentUploadDocumentPathKey;

	private Boolean clientApproval;

	
	
	
   
}
