package com.aashdit.digiverifier.config.admin.model;

import java.io.Serializable;
import java.util.Date;

import com.aashdit.digiverifier.config.superadmin.model.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_dgv_user_token")
public class Token implements Serializable{

	private static final long serialVersionUID = 3513694544081413484L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "token_id")
	private Long tokenId;
	
	@NotBlank
	@Column(name = "user_token")
	private String userToken;
	
	@NotBlank
	@Column(name = "token_type")
	private String tokenType;
	
	@Column(name = "expired")
	private Boolean expired;
	
	@Column(name = "revoked")
	private Boolean revoked;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
}
