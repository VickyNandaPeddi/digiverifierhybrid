package com.aashdit.digiverifier.vendorcheck.dto;

import com.aashdit.digiverifier.config.candidate.model.ConventionalCafAddress;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateCafEducation;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateExperience;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateReferenceInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VendorReferenceDataDto<T> {

    private String candidateId;
    private String CheckName;
    private String dateOfBirth;
    private String fatherName;
    private String gender;
    private String emailId;
    private String contactNumber;
    private List<T> vendorReferenceData;



}
