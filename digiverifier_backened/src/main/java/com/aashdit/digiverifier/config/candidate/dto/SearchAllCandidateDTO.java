package com.aashdit.digiverifier.config.candidate.dto;

import java.util.List;

import lombok.Data;

@Data
public class SearchAllCandidateDTO {
	
	private String agentName;
	private Long userId;
	private String userSearchInput;
	private Long organisationId;
	private String roleName;

}
