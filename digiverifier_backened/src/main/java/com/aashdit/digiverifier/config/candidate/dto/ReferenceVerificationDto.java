package com.aashdit.digiverifier.config.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceVerificationDto {

    private  String referenceName;

    private  String writterOrVerbal;

    private String status;
}
