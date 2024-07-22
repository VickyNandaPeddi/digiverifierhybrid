package com.aashdit.digiverifier.login.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserLoginDto {

	String jwtToken;
	
	String userFirstName;
	
	String roleName;
	
	String organizationId;
	
	String roleCode;
	
	String userId;
	
	boolean orgPassPolicy;
	
	Date lastPasswordUpdated;
}
