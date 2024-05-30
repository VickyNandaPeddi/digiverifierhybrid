package com.aashdit.digiverifier.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PdfUtil {
	
	public static void mergePdfFiles(List<InputStream> inputPdfList,
		OutputStream outputStream) {
//			try{
//				
//				Document document = new Document();
//				PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
//				// pdfWriter.setPageEvent(new FooterPageEvent());
//				document.open();
//				PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
////				HeaderFooterPageEvent headerFooterPageEvent = new HeaderFooterPageEvent();
////				pdfWriter.setPageEvent(headerFooterPageEvent);
//				for(InputStream inputStream : inputPdfList) {
//					PdfReader pdfReader = new PdfReader(inputStream);
//					for(int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
//						document.newPage();
//						PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);
//						pdfContentByte.addTemplate(page, 0, 0);
//					}
//				}
//				
//				outputStream.flush();
//				document.close();
//				outputStream.close();
//			}catch(Exception e){
//				log.error("Exception occured in mergOnlyForThymeleafToPdf method in PdfUtil-->", e);
//			}
		
		float marginLeft = 5;
	    float marginRight = 5;
	    float marginTop = 5;
	    float marginBottom = 50;

	    try {
	        Document document = new Document(PageSize.A4, marginLeft, marginRight, marginTop, marginBottom);
	        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
	        pdfWriter.setPageEvent(new FooterPageEvent());
	        document.open();
	        PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
	        int totalPageCount = 0;
	        int pageNumber = 1;
	        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
	        List<PdfReader> pdfReaders = new ArrayList<>();
	        for (InputStream inputStream1 : inputPdfList) {
	            PdfReader pdfReader = new PdfReader(inputStream1);
	            pdfReaders.add(pdfReader);
	            totalPageCount += pdfReader.getNumberOfPages();
	        }

	        
	        for (PdfReader pdfReader : pdfReaders) {
	            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
	                document.newPage();
	                PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);

	                // Calculate new coordinates with margins
	                float offsetX = marginLeft;
	                float offsetY = marginBottom;
	                float contentWidth = PageSize.A4.getWidth() - marginLeft - marginRight;
	                float contentHeight = PageSize.A4.getHeight() - marginTop - marginBottom;

	                // Add the imported page with margins
	                pdfContentByte.addTemplate(page, contentWidth / page.getWidth(), 0, 0,
	                        contentHeight / page.getHeight(), offsetX, offsetY);
	                
	             // Draw a horizontal line separating content from bottom margin
	                pdfContentByte.setLineWidth(0.5f); // Set line width
	                pdfContentByte.moveTo(20, 60); // Starting point of the line
	                pdfContentByte.lineTo(PageSize.A4.getWidth() - 20, 60); // Ending point of the line
	                pdfContentByte.stroke(); // Draw the line
	                
	             // Add custom text on the left side
	                String leftSideText = "Registered Office: CROSSBOW Global Marketplace\n"
	                		+ "Solutions Private Limited\n"
	                		+ "No.18 & 18/1, Bikaner Signature Towers, Richmond\n"
	                		+ "Rd, Bengaluru, Karnataka 560025.";
	                String[] addressLines = leftSideText.split("\n");
	                for (String line : addressLines) {
	                    pdfContentByte.beginText();
	                    pdfContentByte.setFontAndSize(baseFont, 7);
		                pdfContentByte.setColorFill(BaseColor.GRAY);
		                pdfContentByte.setTextMatrix(25, marginBottom);
	                    pdfContentByte.showText(line);
	                    pdfContentByte.endText();
	                    marginBottom -= 10; // Move to the next line (adjust as needed)
	                }
	                marginBottom=50;
	                String pageText = "Page " + pageNumber + " of " + totalPageCount;
	                pdfContentByte.setFontAndSize(baseFont, 8);
	                pdfContentByte.setColorFill(BaseColor.GRAY);
	                float textWidth = baseFont.getWidthPoint(pageText, 12);
	                float centerX = (PageSize.A4.getWidth() - textWidth) / 2;
	                float centerY = marginBottom;
	                pdfContentByte.beginText();
	                pdfContentByte.setTextMatrix(centerX, centerY);
	                pdfContentByte.showText(pageText);
	                pdfContentByte.endText();
	                pageNumber++;
	            }
	            
	        }

	        outputStream.flush();
	        document.close();
	        outputStream.close();
	    } catch (Exception e) {
	        log.error("Exception occurred in mergePdfFiles method", e);
	    }
	}
	
	private static class FooterPageEvent extends PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfContentByte cb = writer.getDirectContent();
                URL imageUrl = new URL("https://digiverifier-new.s3.ap-south-1.amazonaws.com/Assets/digiverifier_logo_1.png");
                Image img = Image.getInstance(imageUrl);
                img.setAbsolutePosition(430f, 10f);
                img.scaleToFit(350, 50);
                cb.addImage(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

		public static void mergOnlyForThymeleafToPdf(List<InputStream> inputPdfList,
			OutputStream outputStream) {
				try{
					
					Document document = new Document();
					PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
//					pdfWriter.setPageEvent(new FooterPageEvent());
					document.open();
					PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
					for(InputStream inputStream : inputPdfList) {
						PdfReader pdfReader = new PdfReader(inputStream);
						for(int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
							document.newPage();
							PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);
							pdfContentByte.addTemplate(page, 0, 0);
						}
					}
					
					outputStream.flush();
					document.close();
					outputStream.close();
				}catch(Exception e){
					log.error("Exception occured in mergOnlyForThymeleafToPdf method in PdfUtil-->", e);
				}
		}
}
