package com.aashdit.digiverifier.utils;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.vendorcheck.dto.SubmittedCandidates;
import com.aashdit.digiverifier.vendorcheck.service.liCheckToPerformService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CandidateSaveScheduler {
    @Autowired
    liCheckToPerformService liCheckToPerformService;

//    @Scheduled(cron = "${com.dgv.conventionalcandidateSave}")
    public void saveConventionalCandidateInformationScheduler() {
        List<String> collect = null;
        try {
            log.info("candidate save  Schedular Started Successfully At " + new Date());
            ServiceOutcome<SubmittedCandidates> submittedCandidatesServiceOutcome = liCheckToPerformService.saveConventionalVendorSubmittedCandidates("{\"VendorID\":\"2CDC7E3A\"}", true);
            log.info("candidate save  Schedular Started Successfully At " + new Date());

        } catch (Exception e) {
            log.error("Exception occured in saveConventionalCandidateInformationScheduler()  method in CandidateSaveScheduler-->", e);
        }

    }
}
