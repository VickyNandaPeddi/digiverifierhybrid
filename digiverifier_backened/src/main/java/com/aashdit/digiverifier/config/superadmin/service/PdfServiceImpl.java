package com.aashdit.digiverifier.config.superadmin.service;

import com.aashdit.digiverifier.config.candidate.dto.CandidatePurgedPDFReportDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.candidate.dto.ConventionalCandidateDTO;
import com.aashdit.digiverifier.config.candidate.dto.FinalReportDto;
import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAnnotationBorder;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;

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

import java.io.*;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.itextpdf.html2pdf.css.CssConstants.PORTRAIT;

@Slf4j
@Service
public class PdfServiceImpl implements PdfService {

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
            context.setVariable("root", variable);
            // Get the backend.host property value
            String backendHost = envirnoment.getBackendHost();
            System.out.println("BACKEND HOST::::" + backendHost);
            // Determine the CSS path based on the backend.host value
            String cssPath;
            if ("localhost".equalsIgnoreCase(backendHost)) {
                cssPath = envirnoment.getCssPathLocal();
                System.out.println("CSSPATH::::" + cssPath);
            } else {
                cssPath = envirnoment.getCssPathServer();
                System.out.println("CSSPATH::::" + cssPath);
            }
            // Add the CSS path variable to the Thymeleaf context
            context.setVariable("cssPath", cssPath);
            IContext context1 = context;
            return templateEngine.process(templateName, context1);
        } catch (Exception e) {
            log.info("ERROR in GENERATING PDF ::{}", e);
        }
        return "";
    }

    public void generatePdfFromHtml(String html, File report) {
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
            HtmlConverter.convertToPdf(html, pdfDocument, converterProperties);
            outputStream.close();
        } catch (Exception e) {
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
        context.setVariable("root", variable);
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
            context.setVariable("root", variable);
            // Get the backend.host property value
            String backendHost = envirnoment.getBackendHost();
            System.out.println("BACKEND HOST::::" + backendHost);
            // Determine the CSS path based on the backend.host value
            String cssPath;
            if ("localhost".equalsIgnoreCase(backendHost)) {
                cssPath = envirnoment.getCssPathLocal();
                System.out.println("CSSPATH::::" + cssPath);
            } else {
                cssPath = envirnoment.getCssPathServer();
                System.out.println("CSSPATH::::" + cssPath);
            }
            // Add the CSS path variable to the Thymeleaf context
            context.setVariable("cssPath", cssPath);
            IContext context1 = context;
            return templateEngine.process(templateName, context1);
        } catch (Exception e) {
            log.info("ERROR in GENERATING PDF ::{}", e);
        }
        return "";
    }
    
    //	@Override
	public File parseThymeleafTemplate(String techmConventional, ConventionalCandidateDTO variable) {
		try {
			// Configure Thymeleaf template resolver
			ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
			templateResolver.setTemplateMode(TemplateMode.HTML);

			// Prepare Thymeleaf context with variables
			Context context = new Context();
			context.setVariable("panCardVerification", variable.getPanCardVerification());
			context.setVariable("name", variable.getName());
			context.setVariable("root", variable);

			// Get the backend.host property value
			String backendHost = envirnoment.getBackendHost();
			System.out.println("BACKEND HOST::::" + backendHost);

			// Determine the CSS path based on the backend.host value
			String cssPath;
			if ("localhost".equalsIgnoreCase(backendHost)) {
				cssPath = envirnoment.getCssPathLocal();
				System.out.println("CSSPATH::::" + cssPath);
			} else {
				cssPath = envirnoment.getCssPathServer();
				System.out.println("CSSPATH::::" + cssPath);
			}

			// Add the CSS path variable to the Thymeleaf context
			context.setVariable("cssPath", cssPath);
			IContext context1 = context;

			// Process the template to generate HTML content
			String htmlContent = templateEngine.process(techmConventional, context1);

            // Convert HTML to PDF (using iText)
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);

            // Add the FooterEventHelper to handle footer
//            String address = "1234 Street, City, Country";
//            FooterEventHelper footerEventHelper = new FooterEventHelper();
//            pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, footerEventHelper);
//            Document document = new Document(pdfDocument);


            HtmlConverter.convertToPdf(htmlContent, outputStream);
            ByteArrayOutputStream outputStreamWithFooter = new ByteArrayOutputStream();
            PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()));
            PdfWriter pdfWriterWithFooter = new PdfWriter(outputStreamWithFooter);
            PdfDocument pdfDocumentWithFooter = new PdfDocument(pdfReader, pdfWriterWithFooter);
            
            addFooterToAllPages(pdfDocumentWithFooter, "Registered Office: CROSSBOW Global Marketplace\r\n"
            		+ "Solutions Private LimitedNo.18 & 18/1, Bikaner \r\n"
            		+ "Signature Towers, RichmondRd, Bengaluru, Karnataka \r\n"
            		+ "560025",pdfReader,pdfWriterWithFooter);

//            document.close();  // This triggers the END_PAGE event handler

            pdfDocumentWithFooter.close(); 
			byte[] pdfBytes = outputStreamWithFooter.toByteArray();


			// Create a File object and write the PDF bytes to it
			File pdfFile = new File("report.pdf");
			try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
				fos.write(pdfBytes);
			}

			return pdfFile;
		} catch (Exception e) {
			log.info("ERROR in GENERATING PDF ::{}", e);
		}
		return null;
	}


	private void addFooterToAllPages(PdfDocument pdfDocument, String address,PdfReader pdfReader,PdfWriter pdfWriter) {
        int numberOfPages = pdfDocument.getNumberOfPages();
        
//        String logoUrl = "https://digiverifier-new.s3.ap-south-1.amazonaws.com/Assets/digiverifier_logo_1.png";
//        InputStream imageStream = new URL("https://digiverifier-new.s3.ap-south-1.amazonaws.com/Assets/digiverifier_logo_1.png").openStream();
        ImageData imageData = null;
        float logoWidth = 0;
        float logoHeight = 0;
       
		try {
			imageData = ImageDataFactory.create("https://digiverifier-new.s3.ap-south-1.amazonaws.com/Assets/digiverifier_logo_1.png");
			 logoWidth = imageData.getWidth();
			 logoHeight = imageData.getHeight();
		} catch (MalformedURLException e) {
			log.info("PDF LOGO Exceptions : "+e);

		}

        for (int i = 1; i <= numberOfPages; i++) {
            PdfPage pdfPage = pdfDocument.getPage(i);
            Rectangle pageSize = pdfPage.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            
            //LOGO SIZE
            float targetWidth = 130f;  // Desired logo width
            float targetHeight = 70f; // Desired logo height
            Rectangle pageSize2 = pdfPage.getPageSize();
            float xPosition = pageSize2.getRight() - targetWidth - 30;  // 20px padding from the right edge

            float yPosition = pageSize2.getTop() - targetHeight + 10;  // 20px padding from the top edge

            pdfCanvas.addImage(imageData, targetWidth, 0, 0, targetHeight, xPosition, yPosition);
            
            //LOGO SIZE
            
            float footerStartY = pageSize.getBottom() + 60; // Adjust this value as needed

//            pdfCanvas.moveTo(pageSize.getLeft(), pageSize.getBottom() + 35) // Adjust Y coordinate as needed
//            .lineTo(pageSize.getRight(), pageSize.getBottom() + 35) // Draw line across page
//            .stroke();
            
            pdfCanvas.moveTo(pageSize.getLeft(), footerStartY - 5) // Line slightly above the footer
            .lineTo(pageSize.getRight(), footerStartY - 5) // Draw line across the page
            .stroke();

            // Draw "Summary" on the left
            try {
            	 float x = pageSize.getLeft() + 20;
            	    float y = pageSize.getBottom() + 40;
            	
//            	float x = pageSize.getLeft() + 20;
//                float y = pageSize.getBottom() + 100; // Adjusted Y to make sure it's not too close to the bottom



//                pdfCanvas.beginText()
//                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 10)
//                .moveText(x, y)
////                .setFillColor(ColorConstants.BLUE)  // Set text color to blue
//                .showText("Back to Summary")
//                .endText();
            	    
            	    pdfCanvas.setFillColor(ColorConstants.BLUE)  // Set text color to blue
                    .beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD), 10)
                    .moveText(x, y)
                    .showText("Back to Summary")
                    .endText();
            	    
//            	    float textWidth = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN).getWidth("Back to Summary");
//            	    pdfCanvas.setStrokeColor(ColorConstants.BLUE)  // Set underline color to blue
//            	             .setLineWidth(0.5f)  // Set underline thickness
//            	             .moveTo(x, y - 2)  // Position the underline slightly below the text
//            	             .lineTo(x + textWidth, y - 2)  // Extend the line to match the text width
//            	             .stroke();
//            	    
            	    

                
                // Get the PdfPage object for page 2
                PdfPage page2 = pdfDocument.getPage(2);

                // Adjust the Rectangle to cover the text properly
                Rectangle linkArea = new Rectangle(x, y - 3, 80, 10);  // Fine-tuned dimensions to cover the text

//                Rectangle linkArea = new Rectangle(x, y - 10, textWidth, 10);  // Define clickable area

                // Create a link annotation to navigate to page 2
                PdfLinkAnnotation linkAnnotation = new PdfLinkAnnotation(linkArea);
                PdfAction action = PdfAction.createGoTo(PdfExplicitDestination.createFit(page2));  // Navigate to page 2
                linkAnnotation.setAction(action);

                // Optionally set the border of the clickable area for visibility
//                linkAnnotation.setBorder(new PdfAnnotationBorder(1, 1, 1));  // No visible border

                // Add the link annotation to the last page
                pdfPage.addAnnotation(linkAnnotation);
	            
				
			} catch (IOException e) {
				log.info("PDF text back to Summary : "+e);
			}
        
            try {
				pdfCanvas.beginText()
				        .setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 10)
				        .moveText(pageSize.getWidth() / 2 - 20, pageSize.getBottom() + 40)
				        .setFillColor(ColorConstants.BLACK)
				        .showText(String.format("Page %d of %d", i, numberOfPages))
				        .endText();
			} catch (IOException e) {
				log.info("PDF PageNumber exception : "+e);
			}

            // Draw the address on the right
            try {
            	pdfCanvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 8)  // Reduced font size to 8
                .moveText(pageSize.getRight() - 200, pageSize.getBottom() + 40)  // Adjust the X and Y coordinates as needed
                .showText("Registered Office: CROSSBOW Global Marketplace") // First line
                .moveText(0, -10) // Move down 10 units for the next line
                .showText("Solutions Private Limited")
                .moveText(0, -10) // Move down another 10 units for the next line
                .showText("No.18 & 18/1, Bikaner Signature Towers, Richmond Rd")
                .moveText(0, -10) // Move down 10 units for the next line
                .showText("Bengaluru, Karnataka 560025")
                .endText();
			} catch (IOException e) {
				log.info("PDF Address Exception : "+e);
			}

            pdfCanvas.release();
        }
    }
}