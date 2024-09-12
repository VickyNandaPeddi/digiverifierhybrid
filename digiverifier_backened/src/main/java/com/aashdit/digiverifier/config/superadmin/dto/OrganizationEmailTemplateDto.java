package com.aashdit.digiverifier.config.superadmin.dto;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateStatusCountDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationEmailTemplateDto {

	private Long orgId;
	private Long emailTemplateId;
	private String inviteMailSub;
	private String inviteMailContent;
	private String loaMailSub;
	private String loaMailContent;
	private String cwfCopyright;
	private String cwflogo;
	
	
}
