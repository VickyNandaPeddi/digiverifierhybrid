package com.aashdit.digiverifier.config.superadmin.service;

import com.aashdit.digiverifier.config.candidate.dto.CandidatePurgedPDFReportDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.candidate.dto.FinalReportDto;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.File;

public interface PdfService {
	public String parseThymeleafTemplate(String templateName, CandidateReportDTO variable);
	
	public String parseThymeleafTemplate(String templateName, FinalReportDto variable);
	
	public  void generatePdfFromHtml(String html, File report);
	
	public String parseThymeleafTemplate(String templateName, CandidatePurgedPDFReportDto variable);

}
