package com.aashdit.digiverifier.config.superadmin.service;

import com.aashdit.digiverifier.config.candidate.dto.CandidatePurgedPDFReportDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.candidate.dto.ConventionalCandidateDTO;
import com.aashdit.digiverifier.config.candidate.dto.FinalReportDto;
import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
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
            FooterEventHelper footerEventHelper = new FooterEventHelper();
            pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, footerEventHelper);

            System.out.println("pdfDocument :: "+pdfDocument);

            HtmlConverter.convertToPdf(htmlContent, outputStream);
			byte[] pdfBytes = outputStream.toByteArray();

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




//    public File parseThymeleafTemplate(String techmConventional, ConventionalCandidateDTO variable) {
//        try {
//            // Configure Thymeleaf template resolver
//            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//            templateResolver.setTemplateMode(TemplateMode.HTML);
//
//            // Prepare Thymeleaf context with variables
//            Context context = new Context();
//            context.setVariable("panCardVerification", variable.getPanCardVerification());
//            context.setVariable("name", variable.getName());
//            context.setVariable("root", variable);
//
//            // Get the backend.host property value
//            String backendHost = envirnoment.getBackendHost();
//            System.out.println("BACKEND HOST::::" + backendHost);
//
//            // Determine the CSS path based on the backend.host value
//            String cssPath;
//            if ("localhost".equalsIgnoreCase(backendHost)) {
//                cssPath = envirnoment.getCssPathLocal();
//                System.out.println("CSSPATH::::" + cssPath);
//            } else {
//                cssPath = envirnoment.getCssPathServer();
//                System.out.println("CSSPATH::::" + cssPath);
//            }
//
//            // Add the CSS path variable to the Thymeleaf context
//            context.setVariable("cssPath", cssPath);
//
//            // Process the template to generate HTML content
//            String htmlContent = templateEngine.process(techmConventional, context);
//
//            // Convert HTML to PDF (using iText)
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            PdfWriter pdfWriter = new PdfWriter(outputStream);
//            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
//            Document document = new Document(pdfDocument);
//            document.setMargins(5, 5, 50, 5); // Set margins (top, right, bottom, left)
//
//            // Convert HTML to PDF
//            HtmlConverter.convertToPdf(htmlContent, outputStream);
//
//            byte[] pdfBytes = outputStream.toByteArray();
//
//            // Write the PDF bytes to a file
//            File pdfFile = new File("report.pdf");
//            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
//                fos.write(pdfBytes);
//            }
//
//            // Reopen the PDF to add the footer
//            PdfReader pdfReader = new PdfReader("report.pdf");
//            PdfWriter pdfFooterWriter = new PdfWriter("report_with_footer.pdf");
//            PdfDocument pdfDocWithFooter = new PdfDocument(pdfReader, pdfFooterWriter);
//
//            int numberOfPages = pdfDocWithFooter.getNumberOfPages();
//
//            for (int i = 1; i <= numberOfPages; i++) {
//                PdfPage page = pdfDocWithFooter.getPage(i);
//                PdfCanvas pdfCanvas = new PdfCanvas(page);
//                Rectangle pageSize = page.getPageSize();
//
//                // Add footer text on the left side
//                pdfCanvas.beginText()
//                        .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD), 12)
//                        .moveText(pageSize.getLeft() + 36, pageSize.getBottom() + 36) // Position for footer
//                        .showText("Summary")
//                        .endText();
//                pdfCanvas.stroke();
//            }
//
//            // Close the document with footer
//            pdfDocWithFooter.close();
//
//            // Return the new file with the footer
//            return new File("report_with_footer.pdf");
//
//        } catch (Exception e) {
//            log.info("ERROR in GENERATING PDF ::{}", e);
//        }
//        return null;
//    }
}

//
// class FooterEventHandler implements IEventHandler {
//
//    @Override
//    public void handleEvent(Event event) {
//        // Cast the event to PdfDocumentEvent to access the PdfDocument and PdfPage
//        PdfDocumentEvent pdfDocEvent = (PdfDocumentEvent) event;
//        PdfDocument pdfDoc = pdfDocEvent.getDocument();
//        PdfPage pdfPage = pdfDocEvent.getPage();
//        PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
//
//        // Get the page size
//        Rectangle pageSize = pdfPage.getPageSize();
//
//        // Add text to the footer on the left side
//        try {
//            pdfCanvas.beginText()
//                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD), 12)
//                    .moveText(pageSize.getLeft() + 36, pageSize.getBottom() + 36) // Position for footer
//                    .showText(" <a href=\"#summary-section\">Aadhar Verification</a>")
//                    .endText();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        pdfCanvas.stroke();
//    }
//}



 class FooterEventHelper implements IEventHandler {
	 
	 private final String address = "1234 Street, City, Country";
//	    private final PdfFont font;
//
//	    public FooterEventHelper(String address) throws IOException {
//	        this.address = address;
//	        // Load a standard font (Helvetica)
//	        this.font = PdfFontFactory.createFont("Helvetica");
//	    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof PdfDocumentEvent) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage pdfPage = docEvent.getPage();

            PdfCanvas pdfCanvas = new PdfCanvas(pdfPage);
            try {
                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                pdfCanvas.beginText().setFontAndSize(font, 12);
                pdfCanvas.moveText(40, 30); // Adjust coordinates as needed
                pdfCanvas.showText("Summaryyuf"); // Footer text
                pdfCanvas.endText();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pdfCanvas.release();
        }
    }
    
	
    public void handleEvent2(PdfDocumentEvent event) {
        PdfDocument pdfDocument = event.getDocument();
        PdfPage page = event.getPage();
        int pageNumber = pdfDocument.getPageNumber(page);

        // Create a PdfCanvas for drawing the footer
        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDocument);

        Rectangle pageSize = page.getPageSize();
        float y = pageSize.getBottom() + 20;  // Position 20 units from the bottom

        // Draw "Summary" on the left
        try {
			pdfCanvas.beginText()
			        .setFontAndSize(PdfFontFactory.createFont("Helvetica"), 10)
			        .moveText(pageSize.getLeft() + 20, y)
			        .showText("Summary")
			        .endText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Draw the page number in the center
        try {
			pdfCanvas.beginText()
			        .setFontAndSize(PdfFontFactory.createFont("Helvetica"), 10)
			        .moveText(pageSize.getWidth() / 2 - 20, y)
			        .showText(String.format("Page %d of %d", pageNumber, pdfDocument.getNumberOfPages()))
			        .endText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Draw the address on the right
        try {
			pdfCanvas.beginText()
			        .setFontAndSize(PdfFontFactory.createFont("Helvetica"), 10)
			        .moveText(pageSize.getRight() - 200, y)  // Adjust this value based on the address length
			        .showText(address)
			        .endText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        pdfCanvas.release();
    }
}
