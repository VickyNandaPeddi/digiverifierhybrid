package com.aashdit.digiverifier.epfo.remittance.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.epfo.remittance.dto.RemittanceDataFromApiDto;
import com.aashdit.digiverifier.epfo.remittance.dto.RemittanceEmployerRequestDto;
import com.aashdit.digiverifier.epfo.remittance.service.RemittanceService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.websocket.server.PathParam;

@RequestMapping(value = "/api/allowAll")
@RestController
public class RemittanceAccessController {
	
	@Autowired
	private RemittanceService remittanceService;

	@Operation(summary ="getting the remittance details for All employers of candidate from remittance API")
	@GetMapping(value = "/remittanceRecords/{candidateCode}")
	public ServiceOutcome<List<RemittanceDataFromApiDto>> getremittanceRecords(@PathVariable String candidateCode, @RequestParam String flow) throws JsonProcessingException, IOException {

		return remittanceService.getremittanceRecords(candidateCode, flow);

	    }
	
	@Operation(summary ="getting the remittance details for single employers of candidate from remittance API")
	@PostMapping(value = "/remittanceRecordsForEmployer")
	public ServiceOutcome<List<RemittanceDataFromApiDto>> getremittanceRecordsForEmployer(@RequestBody RemittanceEmployerRequestDto remittanceEmployerRequestDto) {

		return remittanceService.getremittanceRecordsForEmployer(remittanceEmployerRequestDto);

	    }
	
	@Operation(summary ="Remove the remittance record from database")
	@GetMapping(value = "/deleteRemittance/{candidateCode}")
	public ServiceOutcome<String> deletedRemittanceRecord(@PathVariable String candidateCode, @RequestParam String memberId,
			@RequestParam String year) {
		
		return remittanceService.deletedRemittanceRecord(candidateCode, memberId, year);

	    }
	
	@Operation(summary ="getting Remittace Captcha Image")
	@GetMapping(value = "/getRemittanceCaptcha/{candidateCode}")
	public ServiceOutcome<String> getRemittanceCaptcha(@PathVariable String candidateCode) {
		
		return remittanceService.getRemittanceCaptcha(candidateCode);

	    }
}
