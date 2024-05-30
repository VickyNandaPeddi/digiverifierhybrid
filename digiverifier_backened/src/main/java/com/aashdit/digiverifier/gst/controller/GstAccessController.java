package com.aashdit.digiverifier.gst.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.gst.dto.GstDataFromApiDto;
import com.aashdit.digiverifier.gst.service.GstService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;


@RequestMapping(value = "/api/allowAll")
@RestController
public class GstAccessController {
	
	@Autowired
	private GstService gstService;

	@Operation(summary ="getting the GST images records from GST Third party API.")
	@GetMapping(value = "/gstRecords/{candidateCode}")
	public ServiceOutcome<List<GstDataFromApiDto>> getGstRecords(@PathVariable String candidateCode, @RequestParam String flow) throws JsonProcessingException, IOException {

		return gstService.getGstRecords(candidateCode, flow);

	    }
	
	@Operation(summary ="Delete the GST images records.")
	@GetMapping(value = "/deleteGstRecord/{gstId}")
	public ServiceOutcome<String> deleteGstRecord(@PathVariable Long gstId) {

		return gstService.deleteGstRecord(gstId);

	}
}
