package com.aashdit.digiverifier.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.ConventionalLoaConsentMaster;
import com.aashdit.digiverifier.config.candidate.model.LoaConsentMaster;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalLoaConsentRepository;
import com.aashdit.digiverifier.config.candidate.repository.LoaConsentMasterRepository;
import com.aashdit.digiverifier.config.superadmin.model.OrganizationConfig;
import com.aashdit.digiverifier.config.superadmin.model.OrganizationEmailTemplate;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationConfigRepository;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationEmailTemplateRepository;
import com.aashdit.digiverifier.email.dto.Email;
import com.aashdit.digiverifier.email.dto.EmailProperties;
import com.itextpdf.io.source.ByteArrayOutputStream;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailSentTask {
	
	@Autowired
	private EmailProperties emailProperties;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private UserRepository userRespository;
	
	@Autowired
	private LoaConsentMasterRepository loaConsentRepository;
	
	@Autowired
	private OrganizationEmailTemplateRepository organizationEmailTemplateRepository;
	
	@Autowired
	private OrganizationConfigRepository organizationConfigRepository;
	
	@Autowired
	private AwsUtils awsUtils;
	
	private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    
	public static final String DIGIVERIFIER_DOC_BUCKET_NAME = "digiverifier-new";
	
	@Autowired
	private ConventionalLoaConsentRepository conventionaLoaConsentRepository;


	
	public Boolean sendEmail(String candidateid,String candidatename,String receiver, String copiedreceiver) throws UnsupportedEncodingException, MessagingException {
		Candidate findByCandidateId = candidateRepository.findByCandidateCode(candidateid);
		Optional<OrganizationConfig> organizationConfig = organizationConfigRepository.findByOrganizationId(findByCandidateId.getOrganization().getOrganizationId());
		Integer inviteExpiryDays = 3;
		if(organizationConfig.isPresent() && organizationConfig.get().getInviteExpiryDays() != null && 
				organizationConfig.get().getInviteExpiryDays()!= 0) {
				inviteExpiryDays = organizationConfig.get().getInviteExpiryDays();
		}
		Boolean result=false;
		Date dateOfExpire = new Date();
		Calendar c = Calendar.getInstance();
        c.setTime(dateOfExpire);
        c.add(Calendar.DATE, inviteExpiryDays);
        c.add(Calendar.HOUR, 1);
//        Date currentDatePlusOne = c.getTime();
		Date currentDatePlusOne = findByCandidateId.getLinkExpireDate()!=null ? findByCandidateId.getLinkExpireDate(): c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        //using subject from organization email templates
        OrganizationEmailTemplate organizationEmailTemplate=organizationEmailTemplateRepository.getByOrganizationId(findByCandidateId.getOrganization().getOrganizationId());
		boolean kpmg = findByCandidateId.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg");
		Email email = new Email();
		if(organizationEmailTemplate!=null && organizationEmailTemplate.getCandidateInviteEmailSub()!=null) {
			email.setTitle(organizationEmailTemplate.getCandidateInviteEmailSub()+findByCandidateId.getApplicantId());
		}else {
			email.setTitle(emailProperties.getDigiverifierEmailTitle()+findByCandidateId.getApplicantId());
		}
		
		if(findByCandidateId.getConventionalCandidate() != null && findByCandidateId.getConventionalCandidate()) {
			email.setContent(prepareContentForDocumentCollection(candidatename,candidateid,dateFormat.format(currentDatePlusOne)));
		} else {
			email.setContent(prepareContent(candidatename,candidateid,dateFormat.format(currentDatePlusOne)));
		}
		email.setSender(kpmg ? emailProperties.getKpmgEmailSenderId() : emailProperties.getDigiverifierEmailSenderId());
		email.setReceiver(receiver);
		email.setBccReceiver(copiedreceiver);
		//email.setCopiedReceiver(copiedreceiver);
		try {
			result=send(email);
			if(result) {
				log.info("Verification Email sent successfully to candidate : "+candidatename+" with candidate id : "+candidateid);
			}else {
				log.info("Verification Email sent unsuccessful to candidate : "+candidatename+" with candidate id : "+candidateid);
			}
		}catch(UnsupportedEncodingException ue) {
			log.info("Sending Email FAILED, Please Check your Email",ue.getMessage());	
			log.error(ue.getMessage()); 
		}catch (MessagingException me) {
			log.error("Sending Email FAILED, Please Check your Email",me.getMessage());
			log.error(me.getMessage());
		}catch (Exception e) {
			log.error("Exception occured : {}",e.getMessage()); 
		}
		return result;
	}


	public Boolean sendAgentEmail(String defaultPassword,String userFirstName,String receiver, String copiedreceiver) throws UnsupportedEncodingException, MessagingException {
		Boolean result=false;
		Date dateOfExpire = new Date();
		Calendar c = Calendar.getInstance();
        c.setTime(dateOfExpire);
        c.add(Calendar.DATE, 3);
        c.add(Calendar.HOUR, 1);
        Date currentDatePlusOne = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Email email = new Email();
		email.setTitle(emailProperties.getDigiverifierEmailTitle());
		email.setContent(prepareContentForAgent(userFirstName,receiver,dateFormat.format(currentDatePlusOne), defaultPassword));
		email.setSender(emailProperties.getDigiverifierEmailSenderId());
		email.setReceiver(receiver);
		email.setCopiedReceiver(copiedreceiver);
		try { 
			result=send(email);
			if(result) {
				log.info("Verification Email sent successfully to candidate : "+userFirstName+" with candidate id : "+receiver);
			}else {
				log.info("Verification Email sent unsuccessful to candidate : "+userFirstName+" with candidate id : "+receiver);
			}
		}catch(UnsupportedEncodingException ue) {
			log.info("Sending Email FAILED, Please Check your Email",ue.getMessage());	
			log.error(ue.getMessage()); 
		}catch (MessagingException me) {
			log.error("Sending Email FAILED, Please Check your Email",me.getMessage());
			log.error(me.getMessage());
		}catch (Exception e) {
			log.error("Exception occured {}",e.getMessage()); 
		}
		return result;
	}
	
	private String prepareContentForAgent(String userFirstName, String userName,String currentDatePlusOne, String defaultPassword) {
		String emailContent = null;
		emailContent = 	"<!DOCTYPE html>"+
						"<html>"+
						"<head>"+
						"<style>a:link, a:visited {"
						+ "  background-color: #36f439;"
						+ "  color: white;"
						+ "  padding: 15px 25px;"
						+ "  text-align: center;"
						+ "  text-decoration: none;"
						+ "  display: inline-block;"
						+ "}"
						
							+ "a:hover, a:active {"
							+ "  background-color: red;"
							+ "}"
						+".center {" + "  margin: auto;" + "width: 90%;"+ "border: 0px solid #73AD21;"+ "text-align: left; "+"padding: 20px;"+ "}"
						+ "</style>"
						+"</head>"
						+"<body>"
						+"<div class="+"center"+">" 
						+"<p>Dear "+userFirstName+" ,<br>"
						+"<br>"
						+"<p>Greetings from Digiverifier!!!</p>"+
						"<p>Your User name: " +userName+"</p>"+
						"<p>Your Password: " +defaultPassword+"</p>"+
						"<p>Please Click on the link Below to become a registered user into the System</p><br>"+
						"<p><a href='"+emailProperties.getDigiverifierAgentUrllink()+userName+"' style='background: #3177eb; color: #fff; font-weight: 500; padding: 8px 20px; font-size: 14px; border-radius: 5px; box-shadow: 0px 2px 2px #00000052'>Sign Up For Digiverifier</a></p>"
						+"<p>On Clicking the above link you will be required to get Started with the SignUp Process</p>"
								
						
						+"<br>"
						+"<br>"
						+"<p>Regards</p>"
						+"<h3>Team - Digiverifier</h3>"		
						+ "</div>"
						+"</body>"
						+"</html>";
		return emailContent;
	}



	private String prepareContent(String candidateName, String candidateId,String currentDatePlusOne) {
		String emailContent = null;
		String organizationName = "Digiverifier!!!";
		Candidate findByCandidateCode = candidateRepository.findByCandidateCode(candidateId);
		
		boolean kpmg = findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg");
		if(kpmg){
			organizationName = "KPMG";
		}
		int stepNumber = 1;
		//using organization email templates from db
		OrganizationEmailTemplate organizationEmailTemplate=organizationEmailTemplateRepository.getByOrganizationId(findByCandidateCode.getOrganization().getOrganizationId());

        // Encode user-generated content for HTML
        String encodedContent = StringEscapeUtils.escapeHtml4(candidateName);
        
		if(organizationEmailTemplate!=null && organizationEmailTemplate.getCandidateInviteEmailTemp()!=null) {
			String inviteTemplate = organizationEmailTemplate.getCandidateInviteEmailTemp();
			List<String> targetBoldWords = List.of("Note", findByCandidateCode.getOrganization().getOrganizationName());
			String updateInviteTemplate =makeWordsBold(inviteTemplate, targetBoldWords);
			
			emailContent = 	"<!DOCTYPE html>"+
					"<html>"+
					"<head>"+
					"<style>"+
					""
					+".center {" + "  margin: auto;" + "width: 90%;"+ "border: 0px solid #73AD21;"+ "text-align: left; "+"padding: 20px;"+ "}"
					+ "</style>"
					+"</head>"
					+"<body>"
					+"<div class="+"center"+">" 
					+"<p>Dear "+encodedContent+",<br>"
					+"<br>"
					+"<pre style=\"font-family: 'Arial', sans-serif; font-size: 14px; color: #333; line-height: 1.5;\">" + updateInviteTemplate + "</pre>"
					+"<br>"
					+"<p><a href='"+emailProperties.getDigiverifierForwardUrllink()+candidateId+"' style='background: #00AE68; color: #fff; font-weight: 500; padding: 8px 20px; font-size: 14px; border-radius: 5px; box-shadow: 0px 2px 2px #00000052'>Start Verification Process</a></p>"
					+"Note: This link will expire on "+currentDatePlusOne
					+ "</div>"
					+"</body>"
					+"</html>";
		}else {
		
		emailContent = 	"<!DOCTYPE html>"+
						"<html>"+
						"<head>"+
						"<style>"+
						""
						+".center {" + "  margin: auto;" + "width: 90%;"+ "border: 0px solid #73AD21;"+ "text-align: left; "+"padding: 20px;"+ "}"
						+ "</style>"
						+"</head>"
						+"<body>"
						+"<div class="+"center"+">" 
						+"<p>Dear "+encodedContent+",<br>"
						+"<br>"
						+"<p>Greetings from "+ organizationName +"!!!</p>"+
						"<p>You have been invited to complete Digital Background Verification </p><br>"+
						
						"<h3>I)   Note:</h3>"+
						"1.	Security and Privacy are important and will remain secure.<br>"+
						"2.	Please provide consent to share your 26AS statement and or EPFO Service History using your ITR login and or EPFO login respectively. If you are a not registered user.<br>"+
						"Click <a href='https://eportal.incometax.gov.in/iec/foservices/#/pre-login/register'>'Register Yourself' to ITR.</a> <br>"+
						"3. Your passwords are encrypted and will not be stored.<br>"+
						"4. Data is used for employment verification purpose only."+
						"<br>"+
						"<br>"+
						
						"<h3>II)   Pre-requisites for initiating the process:</h3>"+
						"1. Keep " + (kpmg ? " " : "Digilocker, ") + "ITR & EPFO login Username & Password handy, please do not share them with anyone.<br>"+
						"2. Your verification cannot be performed without logging into "+(kpmg ? " ":"Digilocker, ")+"ITR & EPFO portals.<br>"+
						"<br>"+
						
						"<h3>III)   Steps to follow:</h3>"+
							" "+stepNumber++ +".Click on Green Button.<br>"+
//							- <a href='"+emailProperties.getDigiverifierForwardUrllink()+candidateId+"'>Start Verification Process</a> here or at the end of this mail
							" "+stepNumber++ +".Submit LOA.<br>"+
							" "+(kpmg? "": stepNumber++ +".Login to Digilocker using Aadhar Number & pin."+"<br>")+
							" "+stepNumber++ +".Login to ITR using PAN, password.<br>"+
							" "+stepNumber++ +"	Don't skip the UAN authentication.<br>"+
							" " + stepNumber++ + ". Login to EPFO page using UAN, password, and Captcha code.<br>"+
//							"7.	<dl>Data Verification Step, check Employer Name and Tenure.<br>"+
//							
//								"<dt>a.		Ensure that you have entered all the companies that you have mentioned in the RESUME even if your employer has not registered<br>"
//								+ "with EPFO. Add employment history & save it and proceed else it’d be considered as undisclosed/ omission of facts in the verification process<br>"
//								+ "for upto 5 years of experience</dt><br>"
//							
//								+ "<dt>b. 		Add all the companies that includes not filed under ITR/ EPFO/ Onsite companies/ Salary Paid in Cash as your Experience in Employment<br>"
//								+ "Validation Form.</dt><br>"
//							
//								+ "<dt>c. 	If your previous employer’s Date of Exit/ LWD is “NOT AVAILABLE”, you may refer your relieving letter/ experience letter for LWD and fill <br>"
//								+ "accordingly.</dt>"
//							+"</dl>"
//						"<br>"
						"<br>"
						+"<p><a href='"+emailProperties.getDigiverifierForwardUrllink()+candidateId+"' style='background: #00AE68; color: #fff; font-weight: 500; padding: 8px 20px; font-size: 14px; border-radius: 5px; box-shadow: 0px 2px 2px #00000052'>Start Verification Process</a></p>"
						+"Note: This link will expire on "+currentDatePlusOne
						+ "</div>"
						+"</body>"
						+"</html>";
		}
		return emailContent;
	}
	
	
	public Boolean send(Email email) throws MessagingException, UnsupportedEncodingException {
		Boolean result=false;
		if(StringUtils.isNotEmpty(email.getSender())) {
			if(StringUtils.isNotEmpty(email.getReceiver())) {
				Properties props = System.getProperties();
				props.put("mail.transport.protocol", emailProperties.getMailTransportProtocol());
				props.put("mail.smtp.port", emailProperties.getSmtpPort());
				props.put("mail.smtp.starttls.enable", emailProperties.getMailStarttlsEnabled());
				props.put("mail.smtp.auth", emailProperties.getMailSmtpAuth());
				Session session = Session.getDefaultInstance(props);

				MimeMessage msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(email.getSender(), emailProperties.getDigiverifierSenderNickName()));
				msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getReceiver()));
				
				if(!StringUtils.isBlank(email.getCopiedReceiver()) || !StringUtils.isBlank(email.getBccReceiver())) {
					if(email.getCopiedReceiver() != null && email.getCopiedReceiver().indexOf(",")>=0 ) {
						String[] recipientList = email.getCopiedReceiver().split(",");
						InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
						int counter = 0;
						for (String recipient : recipientList) {
							log.info("SENDING CC EMAIL TO ::{}",recipient);
						    recipientAddress[counter] = new InternetAddress(recipient.trim());
						    counter++;
						}
						msg.setRecipients(Message.RecipientType.CC, recipientAddress);
					} else {
						
						msg.setRecipients(Message.RecipientType.CC, email.getCopiedReceiver());
						if(email.getBccReceiver() != null) {
							String[] emailAddresses = email.getBccReceiver().split(",");
							for (String emailAddress : emailAddresses) {
							    if (validateEmail(emailAddress.trim())) {
							        log.info("EMAIL is Valid ::: {}", emailAddress);
							        msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(emailAddress));
							    } else {
							        log.warn("Invalid email address: {}", emailAddress);
							    }
							}
							log.info("End of BCC");
						}

					}
					
				}
				msg.setSubject(email.getTitle());
				msg.setContent(email.getContent(), emailProperties.getMailContentType());
				
				// msg.setHeader("X-SES-CONFIGURATION-SET", "ConfigSet");
				Transport transport = session.getTransport();
				try {
					if(Objects.nonNull(email.getAttachmentFile())){
						Multipart multipart = new MimeMultipart(); //1
						// Create the attachment part
						BodyPart attachmentBodyPart = new MimeBodyPart(); //2
						DataSource source = new FileDataSource(email.getAttachmentFile());
						attachmentBodyPart.setDataHandler(new DataHandler(source));
						attachmentBodyPart.setFileName(email.getAttachmentName()); // 2
						multipart.addBodyPart(attachmentBodyPart); //3
						// Create the HTML Part
						BodyPart htmlBodyPart = new MimeBodyPart(); //4
						htmlBodyPart.setContent(email.getContent() , "text/html"); //5
						multipart.addBodyPart(htmlBodyPart); // 6
						// Set the Multipart's to be the email's content
						msg.setContent(multipart); //7
					}
					
					log.info("Trying to open the transport connection");
					transport.connect(emailProperties.getSesHost(), emailProperties.getSesUsername(), emailProperties.getSesPassword());
					transport.sendMessage(msg, msg.getAllRecipients());
					result=true;
				}catch (MessagingException me) {
					log.error("Message Format not Supported .."+me.getMessage());
				} catch (Exception ex) {
					log.error("Message Format not Supported .."+ex.getMessage());
				} finally {
					log.info("Closing the transport connection");
					transport.close();
				}
			}else {
				log.error("Receiver Details not Set/Found");
				throw new MessagingException("Message failed RFC-2822 validation: Receiver Details not Set/Found");
			}
		}else {
			log.error("Sender Details not Set/Found");
			throw new MessagingException("Message failed RFC-2822 validation: Sender Details not Set/Found");
		}
		return result;
	}
	
	public Boolean sendToMultiple(Email email) throws MessagingException, UnsupportedEncodingException {
		Boolean result=false;
		if(StringUtils.isNotEmpty(email.getSender())) {
			if(StringUtils.isNotEmpty(email.getReceiver())) {
				Properties props = System.getProperties();
				props.put("mail.transport.protocol", emailProperties.getMailTransportProtocol());
				props.put("mail.smtp.port", emailProperties.getSmtpPort());
				props.put("mail.smtp.starttls.enable", emailProperties.getMailStarttlsEnabled());
				props.put("mail.smtp.auth", emailProperties.getMailSmtpAuth());
				Session session = Session.getDefaultInstance(props);

				MimeMessage msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(email.getSender(), emailProperties.getDigiverifierSenderNickName()));
					
				if(!email.getReceiver().contains(","))
					msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getReceiver()));
				
				if(!StringUtils.isBlank(email.getReceiver())) {
					if(email.getReceiver() != null && email.getReceiver().indexOf(",")>=0 ) {
						String[] recipientList = email.getReceiver().split(",");
						InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
						int counter = 0;
						for (String recipient : recipientList) {
							log.info("SENDING EMAIL TO :: {} {}", recipient, email.getAttachmentName());
						    recipientAddress[counter] = new InternetAddress(recipient.trim());
						    counter++;
						}
						msg.setRecipients(Message.RecipientType.TO, recipientAddress);
					}
				}
				
				if(!StringUtils.isBlank(email.getCopiedReceiver()) || !StringUtils.isBlank(email.getBccReceiver())) {
					if(email.getCopiedReceiver() != null && email.getCopiedReceiver().indexOf(",")>=0 ) {
						String[] recipientList = email.getCopiedReceiver().split(",");
						InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
						int counter = 0;
						for (String recipient : recipientList) {
							log.info("SENDING CC EMAIL TO :: {} {}", recipient, email.getAttachmentName());
						    recipientAddress[counter] = new InternetAddress(recipient.trim());
						    counter++;
						}
						msg.setRecipients(Message.RecipientType.CC, recipientAddress);
					} else {
						
						msg.setRecipients(Message.RecipientType.CC, email.getCopiedReceiver());
						if(email.getBccReceiver() != null) {
							String[] emailAddresses = email.getBccReceiver().split(",");
							for (String emailAddress : emailAddresses) {
							    if (validateEmail(emailAddress.trim())) {
							        log.info("EMAIL is Valid ::: {}", emailAddress);
							        msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(emailAddress));
							    } else {
							        log.warn("Invalid email address: {}", emailAddress);
							    }
							}
							log.info("End of BCC");
						}

					}
					
				}
				msg.setSubject(email.getTitle());
				msg.setContent(email.getContent(), emailProperties.getMailContentType());
				
				// msg.setHeader("X-SES-CONFIGURATION-SET", "ConfigSet");
				Transport transport = session.getTransport();
				try {
					if(Objects.nonNull(email.getAttachmentFile())){
						Multipart multipart = new MimeMultipart(); //1
						// Create the attachment part
						BodyPart attachmentBodyPart = new MimeBodyPart(); //2
						DataSource source = new FileDataSource(email.getAttachmentFile());
						attachmentBodyPart.setDataHandler(new DataHandler(source));
						attachmentBodyPart.setFileName(email.getAttachmentName()); // 2
						multipart.addBodyPart(attachmentBodyPart); //3
						// Create the HTML Part
						BodyPart htmlBodyPart = new MimeBodyPart(); //4
						htmlBodyPart.setContent(email.getContent() , "text/html"); //5
						multipart.addBodyPart(htmlBodyPart); // 6
						// Set the Multipart's to be the email's content
						msg.setContent(multipart); //7
					}
					
					log.info("Trying to open the transport connection {}", email.getAttachmentName());
					transport.connect(emailProperties.getSesHost(), emailProperties.getSesUsername(), emailProperties.getSesPassword());
					transport.sendMessage(msg, msg.getAllRecipients());
					result=true;
				}catch (MessagingException me) {
					log.error("Message Format not Supported .."+me.getMessage());
				} catch (Exception ex) {
					log.error("Message Format not Supported .."+ex.getMessage());
				} finally {
					log.info("Closing the transport connection");
					transport.close();
				}
			}else {
				log.error("Receiver Details not Set/Found");
				throw new MessagingException("Message failed RFC-2822 validation: Receiver Details not Set/Found");
			}
		}else {
			log.error("Sender Details not Set/Found");
			throw new MessagingException("Message failed RFC-2822 validation: Sender Details not Set/Found");
		}
		return result;
	}
	
	private static boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


	public Boolean sendRelationshipEmail(String candidateCode,String candidatename,String receiver) {
		Boolean result=false;
		Email email = new Email();
		email.setTitle(emailProperties.getDigiverifierEmailTitle());
		email.setContent(prepareRelationshipContent(candidatename,candidateCode));
		email.setSender(emailProperties.getDigiverifierEmailSenderId());
		email.setReceiver(receiver);
		try {
			result=send(email);
			if(!result) {
				log.error("Address Verification Email failed to send to candidate : "+candidatename+" with candidate id : "+candidateCode);
			}
		}catch(UnsupportedEncodingException ue) {
			log.info("Sending Email FAILED, Please Check your Email in sendRelationshipEmail-->",ue);	
			log.error(ue.getMessage()); 
		}catch (MessagingException me) {
			log.error("Sending Email FAILED, Please Check your Email in sendRelationshipEmail-->",me);
			log.error(me.getMessage());
		}catch (Exception e) {
			log.error("Exception occured in sendRelationshipEmail-->",e); 
		}
		return result;
	}
	
	private String prepareRelationshipContent(String candidateName, String candidateId) {
		String emailContent = null;
		emailContent = 	"<!DOCTYPE html>"+
						"<html>"+
						"<head>"+
						"<style>a:link, a:visited {"
						+ "  background-color: #36f439;"
						+ "  color: white;"
						+ "  padding: 15px 25px;"
						+ "  text-align: center;"
						+ "  text-decoration: none;"
						+ "  display: inline-block;"
						+ "}"
						
							+ "a:hover, a:active {"
							+ "  background-color: red;"
							+ "}"
						+".center {" + "  margin: auto;" + "width: 90%;"+ "border: 0px solid #73AD21;"+ "text-align: left; "+"padding: 20px;"+ "}"
						+ "</style>"
						+"</head>"
						+"<body>"
						+"<div class="+"center"+">" 
						+"<p>Dear "+candidateName+" ,<br>"
						+"<br>"
						+"<p>Greetings from Digiverifier!!!</p>"+
						"<p>You have been invited to complete Digital Background Verification </p><br>"+
						
						"<h3>I)   Note:</h3>"+
						"1.	Security and Privacy are important and will remain secure.<br>"+
						"2.	Please provide consent to share your 26AS statement and or EPFO Service <br>"+
						"History using your ITR login and or EPFO login respectively. If you are a not<br>"+
						"registered user, Google ITR registration and or UAN registration respectively<br>"+
						"to register<br>"+
						"3. Your passwords are encrypted and will not be stored."+
						"4. Data is used for employment verification purpose only."+
						"<br>"+
						"<br>"+
						
						"<h3>II)   Pre-requisites for initiating the process:</h3>"+
						"1.	Keep Digilocker, ITR & EPFO login Username & Password handy, please do not share them with anyone.<br>"+
						"2.	Your verification cannot be performed without logging into Digilocker, ITR & EPFO portals.<br>"+
						
						"<h3>III)   Steps to follow:</h3>"+
							"1.	Click on Green Button - \"Start Verification Process\" here or at the end of this mail<br>"+
							"2.	Subbmit LO<br>"+
							"3.	Login to Digilocker & password<br>"+
							"4.	Login to ITR using PAN, password<br>"+
							"5.	Don’t skip the UAN authentication<br>"+
							"6.	Login to EPFO page using UAN, password and Captcha code.<br>"+
							"7.	<dl>Data Verification Step, check Employer Name and Tenure.<br>"+
							
								"<dt>a.		Ensure that you have entered all the companies that you have mentioned in the RESUME even if your employer has not registered<br>"
								+ "with EPFO. Add employment history & save it and proceed else it’d be considered as undisclosed/ omission of facts in the verification process<br>"
								+ "for upto 5 years of experience</dt><br>"
							
								+ "<dt>b. 		Add all the companies that includes not filed under ITR/ EPFO/ Onsite companies/ Salary Paid in Cash as your Experience in Employment<br>"
								+ "Validation Form.</dt><br>"
							
								+ "<dt>c. 	If your previous employer’s Date of Exit/ LWD is “NOT AVAILABLE”, you may refer your relieving letter/ experience letter for LWD and fill <br>"
								+ "accordingly.</dt>"
							+"</dl>"
						+"<br>"
						+"<br>"
						+"<p><a href='"+emailProperties.getDigiverifierRelationshipUrllink()+candidateId+"'>Start Verification Process</a></p>"
						+"</div>"
						+"</body>"
						+"</html>";
		return emailContent;
	}
	
	
	 // Define the convertHtmlToPdf method
    public byte[] convertHtmlToPdf(String htmlContent) throws Exception {
    	if (htmlContent.contains("&")) {
            htmlContent = htmlContent.replace("&", "&amp;");
        }
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.createPDF(baos);
        renderer.finishPDF(); // Optional: Close the renderer to release resources

        return baos.toByteArray();
    }
	
	public void loa(String candidateCode) {
		
		log.info("LOA Method is Calling!!! {}");
		Candidate findByCandidateCode = candidateRepository.findByCandidateCode(candidateCode);
		boolean kpmg = findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String currentDate = sdf.format(date);
       
		String emailContent = null;
		String loaContent =null;
		//using organization email templates from db
		OrganizationEmailTemplate organizationEmailTemplate=organizationEmailTemplateRepository.getByOrganizationId(findByCandidateCode.getOrganization().getOrganizationId());

	 if(findByCandidateCode.getConventionalCandidate() != null && findByCandidateCode.getConventionalCandidate()) {
//		 String imageUrl = "resources/images/prodapt_logo.png";
//         + "<img src=\"" + imageUrl + "\" alt=\"Organization Logo\" />"
//			String imageUrl = "https://digiverifier-new.s3.ap-south-1.amazonaws.com/Assets/digiverifier_logo_1.png";
			 emailContent = "<!DOCTYPE html>"+
					    "<html>"+
					    "<head>"+
					    "<style>a:link, a:visited {"
					    + "  background-color: #36f439;"
					    + "  color: white;"
					    + "  padding: 15px 25px;"
					    + "  text-align: center;"
					    + "  text-decoration: none;"
					    + "  display: inline-block;"
					    + "}"
					    + "body {\r\n"
					    + "            font-size: 20px; /* Adjust the size as needed */\r\n"
					    + "            text-align: justify;\r\n"
					    + "        }"
					    + "a:hover, a:active {"
					    + "  background-color: red;"
					    + "}"
					    + ".center {" + "  margin: auto;" + "width: 90%;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
					    + "</style>"
					    + "</head>"
					    + "<body>"
					    + "<h3 style=\"font-weight: bold; text-align: center; font-size: 24px; text-transform: uppercase;\">LETTER OF AUTHORIZATION</h3>\r\n"
					    + ""
					    + "<div class=\"center\">"  // Fix: Use double quotes to enclose class attribute
					    + "<p><strong>FROM </strong>"+findByCandidateCode.getCandidateName()+",</p>"
					    + "<p><strong>TO </strong>"+findByCandidateCode.getOrganization().getOrganizationName()+"</p><br/>"
					    + "<p>I understand that the information provided by me may be used by Prodapt Solutions Private Limited or any third party agency appointed by the organization to verify and\r\n"
					    + "validate the information I have provided including my employment, my personal background, professional standing, work history and qualifications etc.\r\n"
					    + ".</p>\n"
					    + "<p>I understand that the organization or the third party agency appointed by the organization may obtain information it deems appropriate from various sources including,\r\n"
					    + "but not limited to current and past employers, criminal conviction records, university / school / college records, professional and personal references and other verifying\r\n"
					    + "sources / authorities.\r\n"
					    + "</p>\n"
					    + "<p>I authorize, without reservation, any individual, corporation or other private or public entity to furnish the organization or the third party agency appointed by the\r\n"
					    + "organization, all information about me.</p>\n"
					    + "<p>I unconditionally release and hold harmless any individual, corporation, or private or public entity from any and all causes of action that might arise from furnishing to the\r\n"
					    + "organization or the third party agency appointed by the organization, that they may request pursuant to this release.</p>\n"
					    + "<p>I understand that if I submit any falsified information or documents, I may be subject to legal proceedings and my complete information may be shared with the verifying\r\n"
					    + "source / relevant authorities</p>"
					    + "<p>This authorization and release, in original, faxed or photocopied form, shall be valid for this and any future references.\r\n"
					    + "</p>"
					    + "<br/>"
					    + "<p><strong>Date</strong> : "+currentDate+"</p>"
					    + "</div>"
					    + "</body>"
					    + "</html>";
			 
			 
			 loaContent = "<!DOCTYPE html>"+
					    "<html>"+
					    "<head>"+
					    "<style>a:link, a:visited {"
					    + "  background-color: #36f439;"
					    + "  color: white;"
					    + "  padding: 15px 25px;"
					    + "  text-align: center;"
					    + "  text-decoration: none;"
					    + "  display: inline-block;"
					    + "}"
					    + "body {\r\n"
					    + "            font-size: 15px; /* Adjust the size as needed */\r\n"
					    + "            text-align: justify;\r\n"
					    + "        }"
					    + "a:hover, a:active {"
					    + "  background-color: red;"
					    + "}"
					    + ".center {" + "  margin: auto;" + "width: 90%;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
					    + "</style>"
					    + "</head>"
					    + "<body>"
					    + ""
					    + "<div class=\"center\">"  // Fix: Use double quotes to enclose class attribute
					    + "<p>Dear <strong>"+findByCandidateCode.getCandidateName()+",</strong></p>"
					    + "<p>Greetings from DigiVerifier.\r\n"+ "</p>"
					    + "<p>Further to your consent to perform background Document verification, please find attached the copy of accepted Letter of Authorization.\r\n"+ "</p>"
					    + "<br/>"
					    + "<p>Regards,</p>"
					    + "<p><strong>Team-DigiVerifier</strong> \r\n" + "</p>"
					    + "</div>"
					    + "</body>"
					    + "</html>";
		 
		}		
		else if(organizationEmailTemplate!=null && organizationEmailTemplate.getCandidateLoaEmailTemp()!=null) {
			String loaTemplate = organizationEmailTemplate.getCandidateLoaEmailTemp();
			String loaTemplateEscaped = StringEscapeUtils.escapeHtml4(loaTemplate);
			loaTemplate = loaTemplate.replace("\n", "<br/>");
			// PDF Content
			emailContent = "<!DOCTYPE html>"+
				    "<html>"+
				    "<head>"+
				    "<style>a:link, a:visited {"
				    + "  background-color: #36f439;"
				    + "  color: white;"
				    + "  padding: 15px 25px;"
				    + "  text-align: center;"
				    + "  text-decoration: none;"
				    + "  display: inline-block;"
				    + "}"
				    + "body {\r\n"
				    + "            font-size: 20px; /* Adjust the size as needed */\r\n"
				    + "            text-align: justify;\r\n"
				    + "        }"
				    + "a:hover, a:active {"
				    + "  background-color: red;"
				    + "}"
				    + ".center {" + "  margin: auto;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
				    + "</style>"
				    + "</head>"
				    + "<body>"
				    + "<h3 style=\"font-weight: bold; text-align: center; font-size: 24px; text-transform: uppercase;\">LETTER OF AUTHORIZATION</h3>\r\n"
				    + ""
				    + "<div class=\"center\">"  // Fix: Use double quotes to enclose class attribute
				    + "<p><strong>FROM </strong>"+findByCandidateCode.getCandidateName()+",</p>"
				    + "<br/>"
				    +"<div>"
				    + "<p>" + loaTemplate + "</p>"
				    + "</div>"
				    + "<br/>"
				    + "<p><strong>Date</strong> : "+currentDate+"</p>"
				    + "</div>"
				    + "</body>"
				    + "</html>";
			
			// Mail Content
			 loaContent = "<!DOCTYPE html>"+
					    "<html>"+
					    "<head>"+
					    "<style>a:link, a:visited {"
					    + "  background-color: #36f439;"
					    + "  color: white;"
					    + "  padding: 15px 25px;"
					    + "  text-align: center;"
					    + "  text-decoration: none;"
					    + "  display: inline-block;"
					    + "}"
					    + "body {\r\n"
					    + "            font-size: 15px; /* Adjust the size as needed */\r\n"
					    + "            text-align: justify;\r\n"
					    + "        }"
					    + "a:hover, a:active {"
					    + "  background-color: red;"
					    + "}"
					    + ".center {" + "  margin: auto;" + "width: 90%;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
					    + "</style>"
					    + "</head>"
					    + "<body>"
					    + ""
					    + "<div class=\"center\">"  // Fix: Use double quotes to enclose class attribute
					    + "<p>Dear <strong>"+findByCandidateCode.getCandidateName()+",</strong></p>"
					    + "<p>Greetings from DigiVerifier.\r\n"+ "</p>"
					    + "<br/>"
					    + "<p style=\"font-family: 'Verdana', sans-serif; font-size: 14px; color: #1a1a1a; line-height: 1.4; font-weight: normal;\">Further to your consent to perform background verification, please find attached the copy of accepted Letter of Authorization.\r\n"+ "</p>"
					    + "<br/>"
//					    + "<pre style=\"font-family: 'Verdana', sans-serif; font-size: 14px; color: #1a1a1a; line-height: 1.4; font-weight: normal;\">" + loaTemplateEscaped + "</pre>" 
						+ "<p>Regards,</p>"
						+ "<p><strong>Team-DigiVerifier</strong> \r\n" + "</p>"
					    + "<br/>"
					    + "</div>"
					    + "</body>"
					    + "</html>";
				    
		}
		else {
		// PDF Content
				 emailContent = "<!DOCTYPE html>"+
					    "<html>"+
					    "<head>"+
					    "<style>a:link, a:visited {"
					    + "  background-color: #36f439;"
					    + "  color: white;"
					    + "  padding: 15px 25px;"
					    + "  text-align: center;"
					    + "  text-decoration: none;"
					    + "  display: inline-block;"
					    + "}"
					    + "body {\r\n"
					    + "            font-size: 20px; /* Adjust the size as needed */\r\n"
					    + "            text-align: justify;\r\n"
					    + "        }"
					    + "a:hover, a:active {"
					    + "  background-color: red;"
					    + "}"
					    + ".center {" + "  margin: auto;" + "width: 90%;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
					    + "</style>"
					    + "</head>"
					    + "<body>"
					    + "<h3 style=\"font-weight: bold; text-align: center; font-size: 24px; text-transform: uppercase;\">LETTER OF AUTHORIZATION</h3>\r\n"
					    + ""
					    + "<div class=\"center\">"  // Fix: Use double quotes to enclose class attribute
					    + "<p><strong>FROM </strong>"+findByCandidateCode.getCandidateName()+",</p>"
					    + "<p><strong>TO </strong>"+findByCandidateCode.getOrganization().getOrganizationName()+"</p><br/>"
					    + "<p>I have no objection in authenticating myself and fully understand that information provided by me shall be used for authenticating my identity through Aadhaar Authentication(DigiLocker) System for the purpose stated above and below purpose.</p>\n"
					    + "<p>I have no objection and understand that the information provided by me may be used by the organization or its partner agency to verify and validate the information. I understand that the organization or its partner agency shall receive my electronic EPFO service history and 26AS for employment verification purpose only.</p>\n"
					    + "<p>I authorize, without reservation, any individual, corporation or other private or public entity to furnish the organization or its partner agency the above mentioned information about me.</p>\n"
					    + "<p>I unconditionally release and hold harmless any individual, corporation, or private or public entity from any and all causes of action that might arise from furnishing to the organization or its partner agency that they may request pursuant to this release.</p>\n"
					    + "<p>This authorization and release, in original, electronic form, faxed or photocopied form, shall be valid for this and any future reports and updates that may be requested. This is digitally generated document and signature wont be required.</p>"
					    + "<br/>"
					    + "<p><strong>Date</strong> : "+currentDate+"</p>"
					    + "</div>"
					    + "</body>"
					    + "</html>";
		 
		 // Mail Content
		 loaContent = "<!DOCTYPE html>"+
				    "<html>"+
				    "<head>"+
				    "<style>a:link, a:visited {"
				    + "  background-color: #36f439;"
				    + "  color: white;"
				    + "  padding: 15px 25px;"
				    + "  text-align: center;"
				    + "  text-decoration: none;"
				    + "  display: inline-block;"
				    + "}"
				    + "body {\r\n"
				    + "            font-size: 15px; /* Adjust the size as needed */\r\n"
				    + "            text-align: justify;\r\n"
				    + "        }"
				    + "a:hover, a:active {"
				    + "  background-color: red;"
				    + "}"
				    + ".center {" + "  margin: auto;" + "width: 90%;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
				    + "</style>"
				    + "</head>"
				    + "<body>"
				    + ""
				    + "<div class=\"center\">"  // Fix: Use double quotes to enclose class attribute
				    + "<p>Dear <strong>"+findByCandidateCode.getCandidateName()+",</strong></p>"
				    + "<p>Greetings from DigiVerifier.\r\n"+ "</p>"
				    + "<p>Further to your consent to perform background verification, please find attached the copy of accepted Letter of Authorization.\r\n"+ "</p>"
				    + "<br/>"
				    + "<p>Regards,</p>"
				    + "<p><strong>Team-DigiVerifier</strong> \r\n" + "</p>"
				    + "</div>"
				    + "</body>"
				    + "</html>";
		}
		 // KPMG LOA Content
		 String kpmgLoaContent = "<!DOCTYPE html>"+
				    "<html>"+
				    "<head>"+
				    "<style>"
				    + "body {\r\n"
				    + "            font-size: 15px; /* Adjust the size as needed */\r\n"
				    + "            text-align: justify;\r\n"
				    + "        }"
				    + ".center {" + "  margin: auto;" + "width: 90%;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
				    + ".caution-box {\r\n"
				    + "    border: 1px solid black; /* Black border */\r\n"
				    + "    padding: 10px; /* Adjust padding as needed */\r\n"
				    + "    font-weight: bold; /* Bold text */\r\n"
				    + "    margin-bottom: 20px; /* Adjust the spacing as needed */\r\n"
				    + "}"
				    + ".caution-text {\r\n"
				    + "     color: red; /* Remove default margin */\r\n"
				    + "}"
				    + "</style>"
				    + "</head>"
				    + "<body>"
				    + ""
				    + "<div class=\"center\">"
				    + "<div class=\"caution-box\">\r\n"
				    + "        <p><span class=\"caution-text\">CAUTION:</span> This Email is from an EXTERNAL source. Ensure you trust this sender before clicking on any links or attachments.</p>\r\n"
				    + "    </div>"
				    + "<p>Dear <strong>"+findByCandidateCode.getCandidateName()+",</strong></p>"
				    + "<p>Greetings from KPMG!.\r\n"+ "</p>"
				    + "<p>Thank you for providing your consent for digital employment history verification. Please find attached the copy of accepted Letter of Authorization..\r\n"+ "</p>"
				    + "<br/>"
				    + "<p>Regards,</p>"
				    + "<p><strong style=\"color: blue;\">KPMG</strong> \r\n" + "</p>"
				    + "<br/>"
				    + "<hr class=\"asterisk-line\">"
				    + "<p style=\"font-size: 12px; color: grey; text-decoration: underline;\">DISCLAIMER:</p>"
				    + " <p style=\"font-size: 12px; color: grey;\">\r\n"
				    + "        The information in this e-mail is confidential and may be legally privileged. It is intended solely for the addressee. Access to this e-mail by anyone else is unauthorized. If you have received this communication in error, please address with the subject heading \"Received in error,\" send to <a href=\"mailto:postmaster1@kpmg.com\">postmaster1@kpmg.com</a>, then delete the e-mail and destroy any copies of it. If you are not the intended recipient, any disclosure, copying, distribution or any action taken or omitted to be taken in reliance on it, is prohibited and may be unlawful. Any opinions or advice contained in this e-mail are subject to the terms and conditions expressed in the governing KPMG client engagement letter. Opinions, conclusions and other information in this e-mail and any attachments that do not relate to the official business of the firm are neither given nor endorsed by it.\r\n"
				    + "    </p>"
				    + "<p style=\"font-size: 12px; color: grey;\">\r\n"
				    + "        KPMG cannot guarantee that e-mail communications are secure or error-free, as information could be intercepted, corrupted, amended, lost, destroyed, arrive late or incomplete, or contain viruses.\r\n"
				    + "    </p>"
				    +"<p style=\"font-size: 12px; color: grey;\">\r\n"
				    + "        KPMG, an Indian partnership and a member firm of KPMG International Cooperative (\"KPMG International\"), a Swiss entity that serves as a coordinating entity for a network of independent firms operating under the KPMG name. KPMG International Cooperative (\"KPMG International\") provides no services to clients. Each member firm of KPMG International Cooperative (\"KPMG International\") is a legally distinct and separate entity and each describes itself as such.\r\n"
				    + "    </p>"
				    +"<hr class=\"asterisk-line\">"
				    +" <p style=\"font-size: 10px; color: grey;\">\r\n"
				    + "        <em>&lt;&lt;This is an autogenerated email. Please do not reply to this email.&gt;&gt;</em>\r\n"
				    + "    </p>"
				    + "</div>"
				    + "</body>"
				    + "</html>";
		  
		try {
			String candidateEmail = findByCandidateCode.getCcEmailId();
			String organizationMailId = findByCandidateCode.getOrganization().getOrganizationEmailId();
			String agentMailId = findByCandidateCode.getCreatedBy().getUserEmailId();
			String orgAdminEmailId = "";
			
			Long organizationId = findByCandidateCode.getOrganization().getOrganizationId();
			
			User findByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue = userRespository.findByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue(organizationId, 2L);
			
			if(findByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue.getUserEmailId() != null) {
				orgAdminEmailId =findByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue.getUserEmailId();

			}
			String mergedEmails = candidateEmail + ", " + organizationMailId +", " + agentMailId + ", " + orgAdminEmailId;
						
			log.info("Emails::: {}"+mergedEmails);
			
			byte[] pdfData = convertHtmlToPdf(emailContent);
			File tempFile = FileUtil.createUniqueTempFile("attachment", ".pdf");
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			    fos.write(pdfData);
			}
			Email email = new Email();
			email.setReceiver(findByCandidateCode.getEmailId());
			email.setAttachmentFile(tempFile);
			email.setSender(kpmg ? emailProperties.getKpmgEmailSenderId() : emailProperties.getDigiverifierEmailSenderId());
			if(kpmg) {
				email.setContent(kpmgLoaContent);			
			}
			else {
				email.setContent(loaContent);
			}
			if(organizationEmailTemplate!=null && organizationEmailTemplate.getCandidateLoaEmailSub()!=null) {
				email.setTitle(organizationEmailTemplate.getCandidateLoaEmailSub()+findByCandidateCode.getApplicantId());
			}else {
				email.setTitle("Copy of LOA Acceptance_"+findByCandidateCode.getApplicantId());
			}
			
			//email.setCopiedReceiver(candidateEmail);
			email.setAttachmentName("LOA.pdf");
			email.setBccReceiver(mergedEmails);
			
			log.info("LOA Method is Calling!!! {}");
			Boolean send = send(email);
			// Handle the result, e.g., log or display a success message
		    if (Boolean.TRUE.equals(send)) {
		        log.info("LOA Pdf File Email sent successfully. {}");
//		        Date createdDate = new Date();
//		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		        String formattedDate = dateFormat.format(createdDate);
//		        Date CreatedCurrentDate = dateFormat.parse(formattedDate);
//		    	LoaConsentMaster loaConsent = new LoaConsentMaster();
				String uploadFileAndGetPresignedUrl_bytes = null;
				if (findByCandidateCode.getConventionalCandidate() != null && findByCandidateCode.getConventionalCandidate()) {
					Date createdDate = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String formattedDate = dateFormat.format(createdDate);
					Date CreatedCurrentDate = dateFormat.parse(formattedDate);

					ConventionalLoaConsentMaster conventionalLoa = new ConventionalLoaConsentMaster();

					String path = "ConventionalCandidate/".concat(findByCandidateCode.getCandidateId() + "/Generated"
							.concat("/").concat(findByCandidateCode.getCandidateName() + "_" + "LOA").concat(".pdf"));
					uploadFileAndGetPresignedUrl_bytes = awsUtils
							.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, path, pdfData);

					String base64Encoded = Base64.getEncoder().encodeToString(pdfData);
//				        System.out.println("BASE64::::::::::::"+base64Encoded);
					conventionalLoa.setLoa_consent_detail(path);
					conventionalLoa.setCreatedOn(CreatedCurrentDate);
					conventionalLoa.setCreatedBy(findByCandidateCode.getCreatedBy());
//				        loaConsent.setLastUpdatedOn(findByCandidateCode.getLastUpdatedOn());
//				        loaConsent.setLastUpdatedBy(findByCandidateCode.getLastUpdatedBy());
					conventionalLoa
							.setCandidate(candidateRepository.findByCandidateId(findByCandidateCode.getCandidateId()));

					conventionaLoaConsentRepository.save(conventionalLoa);
					if (conventionalLoa != null) {
						conventionaLoaConsentRepository.save(conventionalLoa);
					}
				} else {
					Date createdDate = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String formattedDate = dateFormat.format(createdDate);
					Date CreatedCurrentDate = dateFormat.parse(formattedDate);
					LoaConsentMaster loaConsent = new LoaConsentMaster();
					String path = "Candidate/".concat(findByCandidateCode.getCandidateId() + "/Generated".concat("/")
							.concat(findByCandidateCode.getCandidateName() + "_" + "LOA").concat(".pdf"));

					uploadFileAndGetPresignedUrl_bytes = awsUtils
							.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, path, pdfData);
					String base64Encoded = Base64.getEncoder().encodeToString(pdfData);
//		        System.out.println("BASE64::::::::::::"+base64Encoded);
					loaConsent.setLoa_consent_detail(path);
					loaConsent.setCreatedOn(CreatedCurrentDate);
					loaConsent.setCreatedBy(findByCandidateCode.getCreatedBy());
//		        loaConsent.setLastUpdatedOn(findByCandidateCode.getLastUpdatedOn());
//		        loaConsent.setLastUpdatedBy(findByCandidateCode.getLastUpdatedBy());
					loaConsent
							.setCandidate(candidateRepository.findByCandidateId(findByCandidateCode.getCandidateId()));

					if (loaConsent != null) {
						loaConsentRepository.save(loaConsent);
					}
				}
 	
		    } else {
		        log.info("LOA Pdf File Email sending failed. {}");
		    }
		 
		} catch (Exception e) {
			log.info(e.getMessage());
		}	    
		
	}
	
	 private static String makeWordsBold(String originalString, List<String> targetWords) {
	        String resultString = originalString;

	        for (String targetWord : targetWords) {
	        	resultString = resultString.replaceAll("(?i)\\b" + targetWord + "\\b", "<b>" + targetWord + "</b>");
	        }

	        return resultString;
	    }
	 
	 public boolean inSufficiencyRaiseEmail(Long candidateId,String remarks,String checkName) {

		 Boolean result=false;
		 String inSuffContent = null;
		 try {

			 Candidate findByCandidateId = candidateRepository.findByCandidateId(candidateId);

			 if(findByCandidateId != null) {

				 String candidateEmailId = findByCandidateId.getEmailId();
				 String agentEmailId = findByCandidateId.getCreatedBy().getUserEmailId();

				 Email email = new Email();
				 

					// Mail Content
					 inSuffContent = "<!DOCTYPE html>"+
							    "<html>"+
							    "<head>"+
							    "<style>a:link, a:visited {"
							    + "  background-color: #36f439;"
							    + "  color: white;"
							    + "  padding: 15px 25px;"
							    + "  text-align: center;"
							    + "  text-decoration: none;"
							    + "  display: inline-block;"
							    + "}"
							    + "body {\r\n"
							    + "            font-size: 15px; /* Adjust the size as needed */\r\n"
							    + "            text-align: justify;\r\n"
							    + "        }"
							    + "a:hover, a:active {"
							    + "  background-color: red;"
							    + "}"
							    + ".center {" + "  margin: auto;" + "width: 90%;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
							    + "</style>"
							    + "</head>"
							    + "<body>"
							    + ""
							    + "<div class=\"center\">"  // Fix: Use double quotes to enclose class attribute
							    + "<p>Dear <strong>"+findByCandidateId.getCandidateName()+",</strong></p>"
							    + "<br/>"
							    + "<p style=\"font-family: 'Verdana', sans-serif; font-size: 14px; color: #1a1a1a; line-height: 1.4; font-weight: normal;\">Check Name - "+checkName +".\r\n"+ "</p>"
//							    + "<br/>"
							    + "<pre style=\"font-family: 'Verdana', sans-serif; font-size: 14px; color: #1a1a1a; line-height: 1.4; font-weight: normal;\">Remarks - " + remarks + "</pre>" 
							    + "<br/>"
							    + "</div>"
							    + "</body>"
							    + "</html>";

				 email.setTitle(findByCandidateId.getCandidateName()+"_"+findByCandidateId.getApplicantId()+" InSufficiency Raised");
				 email.setSender(emailProperties.getDigiverifierEmailSenderId());
				 email.setContent(inSuffContent);
				 email.setReceiver(candidateEmailId);
				 email.setBccReceiver(agentEmailId);

				 result=send(email);

				 if(result) {
					 log.info("InSufficieny Raised Email Sent Successfully");

				 }
				 else {
					 log.info("Something went Wrong in InSufficieny Raised Email Sent");;
				 }

			 }


		 } catch (Exception e) {
			 log.info("EMAIL SENT TASK inSufficiencyRaiseEmail {}");
		 }

		return result;
	 }
	 
	 
		private String prepareContentForDocumentCollection(String candidateName, String candidateId,String currentDatePlusOne) {

			String emailContent = null;

			String organizationName = "Digiverifier!!!";

			Candidate findByCandidateCode = candidateRepository.findByCandidateCode(candidateId);

			boolean kpmg = findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg");

			if(kpmg){

				organizationName = "KPMG";

			}

			int stepNumber = 1;

			//using organization email templates from db

			OrganizationEmailTemplate organizationEmailTemplate=organizationEmailTemplateRepository.getByOrganizationId(findByCandidateCode.getOrganization().getOrganizationId());
	 
	        // Encode user-generated content for HTML

	        String encodedContent = StringEscapeUtils.escapeHtml4(candidateName);

			emailContent = 	"<!DOCTYPE html>"+

							"<html>"+

							"<head>"+

							"<style>"+

							""

							+".center {" + "  margin: auto;" + "width: 90%;"+ "border: 0px solid #73AD21;"+ "text-align: left; "+"padding: 20px;"+ "}"

							+ "</style>"

							+"</head>"

							+"<body>"

							+"<div class="+"center"+">" 

							+"<p>Dear "+encodedContent+",<br>"
							
							+ "<p>Greetings from DigiVerifier.\r\n"+ "</p>"
							
							+"<p>Please upload your documents using the link provided below.</p><br>"

							+"<p><a href='"+emailProperties.getDigiverifierForwardUrllinkForConventional()+candidateId+"' style='background: #00AE68; color: #fff; font-weight: 500; padding: 8px 20px; font-size: 14px; border-radius: 5px; box-shadow: 0px 2px 2px #00000052'>Start Verification Process</a></p>"							

							+"Note: This link will expire on "+currentDatePlusOne

							+ "</div>"

							+"</body>"

							+"</html>";

			return emailContent;

		}

}
