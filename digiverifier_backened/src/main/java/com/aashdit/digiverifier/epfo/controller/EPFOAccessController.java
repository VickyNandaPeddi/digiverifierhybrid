package com.aashdit.digiverifier.epfo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.epfo.dto.EpfoDetailsDto;
import com.aashdit.digiverifier.epfo.service.EpfoService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;

//import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/api/allowAll")
@RestController
public class EPFOAccessController {
	
	@Autowired
	private EpfoService epfoService;
	
	
	/**
	 * 
	 * @param code
	 * @param state
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@Operation(summary = "generating an access token from the EPFO site")
	@GetMapping(value = "/epfoCaptcha/{candidateCode}")
	public ServiceOutcome<EpfoDetailsDto> getEpfoCaptcha(@PathVariable String candidateCode) {
		ServiceOutcome<EpfoDetailsDto> svcSearchResult = new ServiceOutcome<>();
		if(candidateCode!=null) {
			svcSearchResult = epfoService.getEpfoCaptcha(candidateCode);
		}else {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Candidate code is null.");
		}
		
		return svcSearchResult;
    }
	
	@Operation(summary = "Getting the epfo details from EPFO site")
	@PostMapping(value = "/getEpfodetail")
	public ServiceOutcome<String> getEpfodetail(@RequestBody EpfoDetailsDto epfoDetails){
	
//		String candidateId="12345678"; //hard coding done for testing purpose
//		String uanusername="100396510431"; //hard coding done for testing purpose
//		String uanpassword="Sweet123$%^"; //hard coding done for testing purpose
//		String captcha="HVa1q"; //hard coding done for testing purpose
//		String transactionid="106293715193786123033622503445486487686"; //hard coding done for testing purpose
		
		
		ServiceOutcome<String> response =  epfoService.getEpfodetail(epfoDetails);
		return response;
	}

	@Operation(summary = "Getting the epfo details from EPFO site")
	@PostMapping(value = "/getEpfodetailNew")
	public ServiceOutcome<String> getEpfodetailNew(@RequestBody EpfoDetailsDto epfoDetails){
	
//		String candidateId="12345678"; //hard coding done for testing purpose
//		String uanusername="100396510431"; //hard coding done for testing purpose
//		String uanpassword="Sweet123$%^"; //hard coding done for testing purpose
//		String captcha="HVa1q"; //hard coding done for testing purpose
//		String transactionid="106293715193786123033622503445486487686"; //hard coding done for testing purpose
		
		
		ServiceOutcome<String> response =  epfoService.getEpfodetailNew(epfoDetails);
		return response;
	}
	
	@Operation(summary = "generating caaptcha for epfo employee login screen")
	@GetMapping(value = "/epfoLoginCaptcha/{candidateCode}")
	public ServiceOutcome<EpfoDetailsDto> epfoLoginCaptcha(@PathVariable String candidateCode) {
		ServiceOutcome<EpfoDetailsDto> svcSearchResult = new ServiceOutcome<>();
		if(candidateCode!=null) {
			svcSearchResult = epfoService.epfoLoginCaptcha(candidateCode);
		}else {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Candidate code is null.");
		}
		
		return svcSearchResult;
    }
	
	@Operation(summary = "generating caaptcha for epfo employee OTP screen")
	@PostMapping(value = "/epfoOTPScreenCaptcha")
	public ServiceOutcome<EpfoDetailsDto> epfoOTPScreenCaptcha(@RequestBody EpfoDetailsDto epfoDetails) {
		ServiceOutcome<EpfoDetailsDto> svcSearchResult = new ServiceOutcome<>();
		if(epfoDetails.getCandidateCode()!=null) {
			svcSearchResult = epfoService.epfoOTPScreenCaptcha(epfoDetails);
		}else {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Candidate code is null.");
		}
		
		return svcSearchResult;
    }
	
	@Operation(summary = "getting epfo data by epfoOTPCaptchaSubmit OTP screen")
	@PostMapping(value = "/epfoOTPCaptchaSubmit")
	public ServiceOutcome<String> epfoOTPCaptchaSubmit(@RequestBody EpfoDetailsDto epfoDetails) {
		ServiceOutcome<String> svcSearchResult = new ServiceOutcome<>();
		if(epfoDetails.getCandidateCode()!=null) {
			svcSearchResult = epfoService.epfoOTPCaptchaSubmit(epfoDetails);
		}else {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Candidate code is null.");
		}
		
		return svcSearchResult;
    }
	
}
