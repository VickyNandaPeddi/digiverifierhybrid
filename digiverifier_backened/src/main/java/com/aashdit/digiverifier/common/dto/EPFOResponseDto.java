package com.aashdit.digiverifier.common.dto;

import java.util.List;

import com.aashdit.digiverifier.common.util.MessageDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
public class EPFOResponseDto {
    private String code;
    private Boolean success;
    
    @JsonDeserialize(using = MessageDeserializer.class)
    private List<EmploymentDetail> message;
    
    @Data
    public static class EmploymentDetail {
        private String uan;
        private String name;
        private String company;
        private String doe;
        private String doj;
        private String memberId;
    }
}
