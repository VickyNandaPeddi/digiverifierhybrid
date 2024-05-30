package com.aashdit.digiverifier.gst.service;

import java.util.List;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.gst.dto.GstDataFromApiDto;

public interface GstService {

	ServiceOutcome<List<GstDataFromApiDto>> getGstRecords(String candidateCode, String flow);

	ServiceOutcome<String> deleteGstRecord(Long gstId);

}
