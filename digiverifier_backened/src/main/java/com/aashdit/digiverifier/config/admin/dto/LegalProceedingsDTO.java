/**
 * 
 */
package com.aashdit.digiverifier.config.admin.dto;

import java.util.List;

import com.aashdit.digiverifier.config.admin.model.CriminalCheck;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LegalProceedingsDTO {
	
	 private List<CivilProceedingsDTO> civilProceedingsList;
	    private List<CriminalProceedingsDTO> criminalProceedingsList;
	    private List<CriminalCheck> civilProceedingList;
	    private List<CriminalCheck> criminalProceedingList;

}
