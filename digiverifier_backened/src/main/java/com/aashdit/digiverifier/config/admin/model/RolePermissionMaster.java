package com.aashdit.digiverifier.config.admin.model;

import java.io.Serializable;
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

import lombok.Data;

@Data
@Entity
@Table(name = "t_dgv_role_permission_master")
public class RolePermissionMaster implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8671310532887984016L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "permission_id")
	private Long permissionId;
	
	@NotBlank
	@Column(name = "permission_name")
	private String permissionName;
	
	@Column(name = "permission_code")
	private String permissionCode;

	@Column(name = "is_active")
	private Boolean isActive;
	
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
	
	@ManyToOne
	@JoinColumn(name = "permission_head_id")
	private RolePermissionHead rolePermissionHead;
	

}
