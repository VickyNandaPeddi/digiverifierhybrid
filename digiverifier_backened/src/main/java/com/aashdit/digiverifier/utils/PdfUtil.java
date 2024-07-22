package com.aashdit.digiverifier.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
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
	
	static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
	
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
		
		public static byte[] convertXmlToPdf(InputStream xmlInputStream) throws Exception {
	        // Convert XML bytes to PDF using iText
	        try {
	            // Parse the XML file
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            org.w3c.dom.Document xmlDoc = builder.parse(xmlInputStream);
 
	            // Normalize XML structure
	            xmlDoc.getDocumentElement().normalize();
 
	            // Extract data from XML (example with <KycRes> and <UidData>)
	            NodeList nodeList = xmlDoc.getElementsByTagName("KycRes");
	            String name = "";
	            String dob = "";
	            String gender = "";
	            String address = "";
	            String photoBase64 = "";
	            String adharNumber = "";
	            String landMark = "";
	            String city = "";
	            String state = "";
	            String pincode = "";
	            String co="";
 
	            if (nodeList.getLength() > 0) {
	                Node node = nodeList.item(0);
	                if (node.getNodeType() == Node.ELEMENT_NODE) {
	                    Element element = (Element) node;
	                    NodeList uidDataList = element.getElementsByTagName("UidData");
	                    if (uidDataList.getLength() > 0) {
	                        Element uidData = (Element) uidDataList.item(0);
	                        adharNumber = uidData.getAttribute("uid");
	                        Element poi = (Element) uidData.getElementsByTagName("Poi").item(0);
	                        Element poa = (Element) uidData.getElementsByTagName("Poa").item(0);
	                        Element pht = (Element) uidData.getElementsByTagName("Pht").item(0);
 
	                        name = poi.getAttribute("name");
	                        dob = poi.getAttribute("dob");
	                        gender = poi.getAttribute("gender");
	                        address = (poa.hasAttribute("house")? poa.getAttribute("house"): poa.getAttribute("street") )
	                        		  + ", " + (poa.hasAttribute("loc")? poa.getAttribute("loc") : poa.getAttribute("vtc")) + ", " +
	                                poa.getAttribute("dist") + ", " + poa.getAttribute("state") + ", " + poa.getAttribute("pc");
	                        landMark= poa.hasAttribute("lm")? poa.getAttribute("lm"): "-";
	                        photoBase64 = pht.getTextContent();
	                        city= poa.getAttribute("dist");
	                        state= poa.getAttribute("state");
	                        pincode= poa.getAttribute("pc");
	                        co= poa.getAttribute("co");
	                    }
	                }
	            }
 
	            // Create PDF
	            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(byteArrayOutputStream);
	            PdfDocument pdf = new PdfDocument(writer);
	            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);
 
	            // Add table to PDF
	            float[] point1ColumnWidths = {550F};
	            float[] pointColumnWidths = {100F, 450F};
	            float[] point4ColumnWidths = {100F, 175F,100F,175F};
	            float[] point3ColumnWidths = {100F, 225F,225F};
	            
	            Table tableh = new Table(point1ColumnWidths);
	            Cell cell1 = new Cell().add(new com.itextpdf.layout.element.Paragraph("Digilocker verified e-Aadhar").setFontSize(12f).setBold().setTextAlignment(TextAlignment.CENTER));
	            tableh.addCell(cell1.setBorder(Border.NO_BORDER));
	            Cell cell2 = new Cell().add(new com.itextpdf.layout.element.Paragraph("\n"));
	            tableh.addCell(cell2.setBorder(Border.NO_BORDER));
	            Cell cell3 =new Cell().add(new com.itextpdf.layout.element.Paragraph("The document is generated from verifier XML obtained from Digilocker with due user consent and authentication").setFontSize(10f).setTextAlignment(TextAlignment.CENTER));
	            tableh.addCell(cell3.setBorder(Border.NO_BORDER));
	            tableh.addCell(cell2.setBorder(Border.NO_BORDER));
	            document.add(tableh);
	            
	            Table table0 = new Table(pointColumnWidths);
 
	            table0.addCell("Document Type");
	            table0.addCell("e-Aadhaar generated from Digilocker verified Aadhaar XML");
	            document.add(table0);
	            
	            Table table4C = new Table(point4ColumnWidths);
	            try {
	            	table4C.addCell("Generation date");
					table4C.addCell(formatter.format(new Date()));
					table4C.addCell("Download date");
		            table4C.addCell(formatter.format(new Date()));
				} catch (Exception e) {
					log.info("Exception in parsing of adhar dates::{}",e);
				}
	            document.add(table4C);
	            
	            Table table1 = new Table(pointColumnWidths);
	            table1.addCell("Masked Aadhaar Number");
	            table1.addCell(new com.itextpdf.layout.element.Paragraph(adharNumber).setFontSize(12f).setBold());
	            
	            document.add(table1);
 
	            Table table3C = new Table(point3ColumnWidths);
	            table3C.addCell("Name");
	            table3C.addCell(name);
	            
	         // Add image to PDF
	            if (!photoBase64.isEmpty()) {
	                byte[] photoBytes = Base64.getDecoder().decode(photoBase64);
	                com.itextpdf.layout.element.Image photo = new com.itextpdf.layout.element.Image(ImageDataFactory.create(photoBytes));
	                photo.setAutoScale(true);
	                Cell photoCell = new Cell(5, 1); // Spanning 5 rows in the third column
	                photoCell.add(photo);
	                table3C.addCell(photoCell);
	            }
	            
 
	            table3C.addCell("Date of Birth");
	            table3C.addCell(dob);
 
	            table3C.addCell("Gender");
	            table3C.addCell(gender);
	            
	            table3C.addCell("C/O, S/O, D/O");
	            table3C.addCell(co);
 
	            table3C.addCell("Address");
	            table3C.addCell(address);
 
	            document.add(table3C);
	            
	            Table table4C1 = new Table(point4ColumnWidths);
	            table4C1.addCell("Landmark");
	            table4C1.addCell(landMark);
 
	            table4C1.addCell("Locality");
	            table4C1.addCell("-");
	            document.add(table4C1);
	            
	            Table table = new Table(pointColumnWidths);
	            table.addCell("City/District");
	            table.addCell(city);
	            document.add(table);
	            
	            Table table4C2 = new Table(point4ColumnWidths);
	            table4C2.addCell("Pin Code");
	            table4C2.addCell(pincode);
 
	            table4C2.addCell("State");
	            table4C2.addCell(state);
	            document.add(table4C2);
 
 
	            // Close document
	            document.close();
 
	            return byteArrayOutputStream.toByteArray();
	        } catch (Exception e) {
	        	log.error("Exception in xml to pdf converter ::{}",e);
	            return null;
	        
	        }
 
	    }
		
		public static void mergePurgedPdfFiles(List<InputStream> inputPdfList,
				OutputStream outputStream) {
				
				float marginLeft = 5;
			    float marginRight = 5;
			    float marginTop = 5;
			    float marginBottom = 50;

			    try {
			        Document document = new Document(PageSize.A4, marginLeft, marginRight, marginTop, marginBottom);
			        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
			        pdfWriter.setPageEvent(new purgeFooterPageEvent());
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
//			                pdfContentByte.setLineWidth(0.5f); // Set line width
//			                pdfContentByte.moveTo(20, 60); // Starting point of the line
//			                pdfContentByte.lineTo(PageSize.A4.getWidth() - 20, 60); // Ending point of the line
//			                pdfContentByte.stroke(); // Draw the line
			                
			             // Add custom text on the left side
//			                String leftSideText = "Registered Office: CROSSBOW Global Marketplace\n"
//			                		+ "Solutions Private Limited\n"
//			                		+ "No.18 & 18/1, Bikaner Signature Towers, Richmond\n"
//			                		+ "Rd, Bengaluru, Karnataka 560025.";
//			                String[] addressLines = leftSideText.split("\n");
//			                for (String line : addressLines) {
//			                    pdfContentByte.beginText();
//			                    pdfContentByte.setFontAndSize(baseFont, 7);
//				                pdfContentByte.setColorFill(BaseColor.GRAY);
//				                pdfContentByte.setTextMatrix(25, marginBottom);
//			                    pdfContentByte.showText(line);
//			                    pdfContentByte.endText();
//			                    marginBottom -= 10; // Move to the next line (adjust as needed)
//			                }
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
			
			private static class purgeFooterPageEvent extends PdfPageEventHelper {
		        public void onEndPage(PdfWriter writer, Document document) {
		            try {
		                PdfContentByte cb = writer.getDirectContent();
		                URL imageUrl = new URL("https://digiverifier-new.s3.ap-south-1.amazonaws.com/Assets/digiverifier_logo_1.png");
		                Image img = Image.getInstance(imageUrl);
		                img.setAbsolutePosition(450f, 802f);
		                img.scaleToFit(350, 50);
		                cb.addImage(img);
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
		        }
		    }
}
