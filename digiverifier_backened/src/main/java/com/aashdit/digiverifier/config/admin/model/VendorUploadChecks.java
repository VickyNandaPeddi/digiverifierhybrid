package com.aashdit.digiverifier.config.admin.model;
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
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.superadmin.model.Source;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.superadmin.model.Color;
// import com.aashdit.digiverifier.config.admin.model.VendorMasterNew;
import lombok.Data;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import jakarta.persistence.OneToOne;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
@Table(name = "t_dgv_vendor_uploaded_checks")

	/**
	 * 
	 */
public class VendorUploadChecks implements Serializable {
	// private static final long serialVersionUID = 5043587024437591514L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vendor_upload_check_id")
	private Long vendoruploadcheckId;
	

	@NotNull
	@OneToOne
	@JoinColumn(name = "vendor_check_id")
	private VendorChecks vendorChecks;
	

	@Column(name = "document_name")
	private String documentname;
	
	//@Type(type="org.hibernate.type.BinaryType")
	@JdbcTypeCode(Types.BINARY)
    @Column(name = "vendor_Uploaded_Document", columnDefinition="LONGBLOB")
    private byte[] vendorUploadedDocument;

	@OneToOne
	@JoinColumn(name = "agent_color")
	private Color AgentColor;


	@ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private Date createdOn;

    @Lob
    @Column(name = "vendor_attribute_value", columnDefinition = "BLOB")
     private ArrayList<String> vendorAttirbuteValue;	
    
    @Lob
    @Column(name = "vendor_uploaded_image", columnDefinition = "LONGBLOB")
    private String vendorUploadedImage;
    
    @Column(name = "vendor_upload_image_path_key")
    private String vendorUploadImagePathKey;

    @Column(name = "vendor_upload_document_path_key")
    private String vendorUploadDocumentPathKey;
    
    @Column(name = "conventional_qc_pending")
    private Boolean conventionalQcPending;

}
