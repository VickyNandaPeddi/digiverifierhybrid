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
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "t_dgv_user_role_permission_map")
public class UserRolePermissionMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5043587024437591514L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_role_permission_map_id")
	private Long permissionMapId;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "permission_id")
	private RolePermissionMaster rolePermissionMaster;
	
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
