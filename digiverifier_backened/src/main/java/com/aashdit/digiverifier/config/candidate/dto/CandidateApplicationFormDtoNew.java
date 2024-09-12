package com.aashdit.digiverifier.config.candidate.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class CandidateApplicationFormDtoNew {


    private String candidateName;

    private String dateOfBirth;

    private String panNumber;

    private String candidateUan;

    private List<CandidateCafEducationDto> candidateCafEducationDto;

    private List<CandidateCafExperienceDto> candidateCafExperienceDto;

    private List<CandidateCafAddressDto> candidateCafAddressDto;

    private Boolean isFresher;

    private Boolean isUanSkipped;


    private Boolean conventionalCandidate;
}


