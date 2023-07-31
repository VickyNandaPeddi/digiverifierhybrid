package com.aashdit.digiverifier.config.candidate.dto;

import com.aashdit.digiverifier.epfo.model.EpfoData;
import lombok.Data;

import java.util.List;

@Data
public class SuspectEmpMasterDto {
	
	private Boolean isActive;
	
	private String address;
	
	private Long id;

	private String suspectCompanyName;

}
