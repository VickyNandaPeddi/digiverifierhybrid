package com.aashdit.digiverifier.config.superadmin.service;

import com.aashdit.digiverifier.config.candidate.dto.CandidatePurgedPDFReportDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.candidate.dto.FinalReportDto;
import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import static com.itextpdf.html2pdf.css.CssConstants.PORTRAIT;

@Slf4j
@Service
public class PdfServiceImpl implements PdfService{
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private EnvironmentVal envirnoment;
	
	public String parseThymeleafTemplate(String templateName, CandidateReportDTO variable) {
		try {
			ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	//		templateResolver.setSuffix(".html");
			templateResolver.setTemplateMode(TemplateMode.HTML);
			Context context = new Context();
			context.setVariable("panCardVerification", variable.getPanCardVerification());
			context.setVariable("name", variable.getName());
			context.setVariable("root",variable);
			// Get the backend.host property value
		    String backendHost = envirnoment.getBackendHost();
		    System.out.println("BACKEND HOST::::"+backendHost);
		    // Determine the CSS path based on the backend.host value
		    String cssPath;
		    if ("localhost".equalsIgnoreCase(backendHost)) {
		        cssPath = envirnoment.getCssPathLocal();
		        System.out.println("CSSPATH::::"+cssPath);
		    } else {
		        cssPath = envirnoment.getCssPathServer();
		        System.out.println("CSSPATH::::"+cssPath);
		    }
		    // Add the CSS path variable to the Thymeleaf context
		    context.setVariable("cssPath", cssPath);
			IContext context1 = context;
			return templateEngine.process(templateName, context1);
		}catch(Exception e) {
			log.info("ERROR in GENERATING PDF ::{}", e);
		}
		return "";
	}
	
	public  void generatePdfFromHtml(String html, File report) {
		try {
			OutputStream outputStream = new FileOutputStream(report);
			ConverterProperties converterProperties = new ConverterProperties();
			PdfWriter pdfWriter = new PdfWriter(outputStream);
			PdfDocument pdfDocument = new PdfDocument(pdfWriter);
			pdfDocument.setDefaultPageSize(new PageSize(PageSize.A4));
			MediaDeviceDescription mediaDeviceDescription = new MediaDeviceDescription(MediaType.PRINT);
			converterProperties.setMediaDeviceDescription(mediaDeviceDescription);
//			pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, new HeaderHandler());
//			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterHandler());
			HtmlConverter.convertToPdf(html,pdfDocument,converterProperties);
			outputStream.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
	}

	@Override
	public String parseThymeleafTemplate(String templateName, FinalReportDto variable) {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		Context context = new Context();
//		context.setVariable("panCardVerification", variable.getPanCardVerification());
		context.setVariable("name", variable.getName());
		context.setVariable("root",variable);
		IContext context1 = context;
		return templateEngine.process(templateName, context1);
	}

	@Override
	public String parseThymeleafTemplate(String templateName, CandidatePurgedPDFReportDto variable) {
		try {
			ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	//		templateResolver.setSuffix(".html");
			templateResolver.setTemplateMode(TemplateMode.HTML);
			Context context = new Context();
//			context.setVariable("panCardVerification", variable.getPanCardVerification());
//			context.setVariable("name", variable.getName());
			context.setVariable("root",variable);
			// Get the backend.host property value
		    String backendHost = envirnoment.getBackendHost();
		    System.out.println("BACKEND HOST::::"+backendHost);
		    // Determine the CSS path based on the backend.host value
		    String cssPath;
		    if ("localhost".equalsIgnoreCase(backendHost)) {
		        cssPath = envirnoment.getCssPathLocal();
		        System.out.println("CSSPATH::::"+cssPath);
		    } else {
		        cssPath = envirnoment.getCssPathServer();
		        System.out.println("CSSPATH::::"+cssPath);
		    }
		    // Add the CSS path variable to the Thymeleaf context
		    context.setVariable("cssPath", cssPath);
			IContext context1 = context;
			return templateEngine.process(templateName, context1);
		}catch(Exception e) {
			log.info("ERROR in GENERATING PDF ::{}", e);
		}
		return "";
	}
}
