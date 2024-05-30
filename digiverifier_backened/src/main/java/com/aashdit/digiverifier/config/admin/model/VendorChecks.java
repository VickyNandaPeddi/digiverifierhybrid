package com.aashdit.digiverifier.config.admin.model;

// import java.io.Serializable;
// import java.util.Date;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import jakarta.validation.constraints.NotNull;
// import lombok.Data;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.superadmin.model.Source;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
// import com.aashdit.digiverifier.config.admin.model.VendorMasterNew;
import lombok.Data;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import com.aashdit.digiverifier.config.superadmin.model.VendorCheckStatusMaster;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)

@Data
@Entity
@Table(name = "t_dgv_vendor_checks")
public class VendorChecks implements Serializable {

	/**
	 * 
	 */
	// private static final long serialVersionUID = 5043587024437591514L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vendor_check_id")
	private Long vendorcheckId;
	
	@ManyToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;
	
	@Column(name = "vendor_id")
	private Long vendorId;
	
	@ManyToOne
	@JoinColumn(name = "source_id")
	private Source source;
	
	@Column(name = "tat")
    private Double tat;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "expires_on")
	private Boolean expireson;


	//@Type(type="org.hibernate.type.BinaryType")
	@JdbcTypeCode(Types.BINARY)
    @Column(name = "agent_Uploaded_Document", columnDefinition="LONGBLOB")
    private byte[] agentUploadedDocument;
	

	@ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private Date createdOn;

	@Column(name = "Is_proof_uploaded")
	private Boolean Isproofuploaded;

	@Column(name = "document_name")
	private String documentname;

	@Column(name = "candidate_name")
	private String candidateName;

	@Column(name = "date_of_birth")
	private String dateOfBirth;

	@Column(name = "contact_no")
	private String contactNo;

	@Column(name = "father_name")
	private String fatherName;

	@Column(name = "address")
	private String address;

	@Column(name = "alternate_contact_no")
	private String alternateContactNo;

	@Column(name = "type_of_panel")
	private String typeOfPanel;

	@ManyToOne
	@JoinColumn(name = "vendor_checkstatus_master_id")
	private VendorCheckStatusMaster vendorCheckStatusMaster;
	
	@Lob
    @Column(name = "vender_attribute_value", columnDefinition = "BLOB")
    private ArrayList<String> venderAttirbuteValue;
	
	@Lob
    @Column(name = "agent_attribute_value", columnDefinition = "BLOB")
      private ArrayList<String> agentAttirbuteValue;
	
	@Column(name = "stop_check")
	private Boolean stopCheck;
	
	@Column(name = "stop_check_created_on")
	private Date stopCheckCreatedOn;
	
	@Column(name = "insufficiency_remarks")
	private String inSufficiencyRemarks;
	
	@Column(name = "check_type")
	private String checkType;
	
	@Column(name = "agent_upload_image_path_key")
	private String agentUploadImagePathKey;

	@Column(name = "agent_upload_document_path_key")
	private String agentUploadDocumentPathKey;

}
