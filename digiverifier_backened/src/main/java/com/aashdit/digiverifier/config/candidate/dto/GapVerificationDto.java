package com.aashdit.digiverifier.config.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GapVerificationDto {

    private  String gapBetween;

    private  String periodOfGap;

    private String reasonForGap;
}
