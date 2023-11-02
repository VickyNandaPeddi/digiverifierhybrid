package com.aashdit.digiverifier.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.aashdit.digiverifier.vendorcheck.model.ConventionalVendorCandidatesSubmitted;
import com.aashdit.digiverifier.vendorcheck.model.ConventionalVendorliChecksToPerform;
import com.aashdit.digiverifier.vendorcheck.model.LicheckHistory;
import com.aashdit.digiverifier.vendorcheck.repository.LicheckHistoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aashdit.digiverifier.email.dto.Email;
import com.aashdit.digiverifier.email.dto.EmailProperties;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailSentTask {

    @Autowired
    private EmailProperties emailProperties;

    @Autowired
    private LicheckHistoryRepository licheckHistoryRepository;
    @Autowired
    EnvironmentVal environmentVal;
    public  String prepareContentForCandidateData(List<ConventionalVendorCandidatesSubmitted> conventionalVendorCandidatesSubmittedList) {
        String emailContent = null;
        StringBuilder tableContent = new StringBuilder();
        // Start the table
        tableContent.append("<table border=\"1\">");
        tableContent.append("<tr>");
        tableContent.append("<th>Agent Name</th>");
        tableContent.append("<th>Candidate Name</th>");
        tableContent.append("<th>Candidate ID</th>");
        tableContent.append("<th>PS.No</th>");
        tableContent.append("<th>Request Type</th>");
        tableContent.append("<th>Request ID</th>");
        tableContent.append("<th>Fast Track</th>");
        tableContent.append("<th>Stop Check Received Date</th>");
        tableContent.append("<th>Data Uploaded</th>");
        tableContent.append("<th>Status</th>");
        tableContent.append("</tr>");

        // Iterate through the list and generate a row for each item
        for (ConventionalVendorCandidatesSubmitted item : conventionalVendorCandidatesSubmittedList) {
            tableContent.append("<tr>");
            tableContent.append("<td>").append(item.getCreatedBy().getUserName()).append("</td>");
            tableContent.append("<td>").append(item.getName()).append("</td>");
            tableContent.append("<td>").append(item.getCandidateId()).append("</td>");
            tableContent.append("<td>").append(item.getPsNo()).append("</td>");
            tableContent.append("<td>").append(item.getRequestType()).append("</td>");
            tableContent.append("<td>").append(item.getRequestId()).append("</td>");
            tableContent.append("<td>").append(item.getFastTrack()).append("</td>");
            tableContent.append("<td>").append(item.getStopCheckRecivedDate()).append("</td>");
            tableContent.append("<td>").append(item.getCreatedOn()).append("</td>");
            tableContent.append("<td>").append(item.getStatus().getStatusCode()).append("</td>");
            tableContent.append("</tr>");
        }
        // Close the table
        tableContent.append("</table>");

        emailContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                // Add your styles here
                "</head>" +
                "<body>" +
                "<div class=\"center\">" +
                "<p>Dear Admin,<br><br>" +
                "<p>LTM has added the following new candidates. Your action is required on these candidates.</p><br>" +
                "<p> Server  Name :"+environmentVal.getDigiverifierFrontendHost()+"</p><br>" +
                "</div>" +
                tableContent +
                "</body>" +
                "</html>";
        return emailContent;
    }

    public String prepareContentForInsufficiencyChecks(ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted, List<ConventionalVendorliChecksToPerform> conventionalVendorliChecksToPerforms) {
        String candidateContent = null;
        StringBuilder tableContent = new StringBuilder();
        // Start the table
        tableContent.append("<table border=\"1\">");
        tableContent.append("<tr>");
        tableContent.append("<th>Agent Name</th>");
        tableContent.append("<th>Candidate Name</th>");
        tableContent.append("<th>Candidate ID</th>");
        tableContent.append("<th>PS.No</th>");
        tableContent.append("<th>Request Type</th>");
        tableContent.append("<th>Request ID</th>");
        tableContent.append("<th>Fast Track</th>");
        tableContent.append("<th>Stop Check Received Date</th>");
        tableContent.append("<th>Data Uploaded</th>");
        tableContent.append("<th>Status</th>");
        tableContent.append("</tr>");
        // Iterate through the list and generate a row for each item
        tableContent.append("<tr>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getCreatedBy().getUserName()).append("</td>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getName()).append("</td>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getCandidateId()).append("</td>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getPsNo()).append("</td>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getRequestType()).append("</td>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getRequestId()).append("</td>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getFastTrack()).append("</td>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getStopCheckRecivedDate()).append("</td>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getCreatedOn()).append("</td>");
        tableContent.append("<td>").append(conventionalVendorCandidatesSubmitted.getStatus().getStatusCode()).append("</td>");
        tableContent.append("</tr>");
        tableContent.append("</table>");
        StringBuilder checksContent = new StringBuilder();
        // Start the table
        checksContent.append("<table border=\"1\">");
        checksContent.append("<tr>");
        checksContent.append("<th>Check Unique Id</th>");
        checksContent.append("<th>Check Code</th>");
        checksContent.append("<th>Check Name</th>");
        checksContent.append("<th>Check Status</th>");
        checksContent.append("<th>Verification Required</th>");
        checksContent.append("</tr>");
        // Iterate through the list and generate a row for each item
        for (ConventionalVendorliChecksToPerform item : conventionalVendorliChecksToPerforms) {
            checksContent.append("<tr>");
            checksContent.append("<td>").append(item.getCheckUniqueId()).append("</td>");
            checksContent.append("<td>").append(item.getCheckCode()).append("</td>");
            checksContent.append("<td>").append(item.getCheckName()).append("</td>");
            checksContent.append("<td>").append(item.getCheckStatus().getCheckStatusCode()).append("</td>");
            checksContent.append("<td>").append(item.getModeOfVerificationRequired()).append("</td>");
            checksContent.append("</tr>");
        }
        checksContent.append("</table>");
        candidateContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                // Add your styles here
                "</head>" +
                "<body>" +
                "<div class=\"center\">" +
                "<p>Dear Admin,<br><br>" +
                "<p>LTM has Resubmitted  the following Checks for the Canddiate. Your action is required on these Checks.</p><br>" +
                "<p> Server  Name :"+environmentVal.getDigiverifierFrontendHost()+"</p><br>" +
                "</div>" +
                tableContent +
                checksContent +
                "</body>" +
                "</html>";
        return candidateContent;
    }
    public Boolean sendEmailOnSaveCandidates(List<ConventionalVendorCandidatesSubmitted> conventionalVendorCandidatesSubmittedList, String receiver, String copiedreceiver) throws UnsupportedEncodingException, MessagingException {
        Boolean result = false;
        Date dateOfExpire = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dateOfExpire);
        c.add(Calendar.DATE, 3);
        c.add(Calendar.HOUR, 1);
        Date currentDatePlusOne = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Email email = new Email();
        email.setTitle(emailProperties.getDigiverifierEmailTitle());
        email.setContent(prepareContentForCandidateData(conventionalVendorCandidatesSubmittedList));
        email.setSender(emailProperties.getDigiverifierEmailSenderId());
        email.setReceiver(receiver);
        email.setCopiedReceiver(copiedreceiver);
        try {
            result = send(email);
            if (result) {
                log.info("Verification Email sent successfully to Digiverifier Team");
            } else {
                log.info("Verification Email sent unsuccessful to Digiverifier Team");
            }
        } catch (UnsupportedEncodingException ue) {
            log.info("Sending Email FAILED, Please Check your Email", ue.getMessage());
            log.error(ue.getMessage());
        } catch (MessagingException me) {
            log.error("Sending Email FAILED, Please Check your Email", me.getMessage());
            log.error(me.getMessage());
        } catch (Exception e) {
            log.error("Exception occured", e.getMessage());
        }
        return result;
    }

    public Boolean sendEmailOnRaisedInsufficiency(ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted, List<ConventionalVendorliChecksToPerform> conventionalVendorliChecksToPerforms, String receiver, String copiedreceiver) throws UnsupportedEncodingException, MessagingException {

        for (ConventionalVendorliChecksToPerform conventionalVendorliChecksToPerform : conventionalVendorliChecksToPerforms) {
            LicheckHistory licheckHistory = new LicheckHistory();
            licheckHistory.setCandidateId(Long.valueOf(conventionalVendorliChecksToPerform.getCandidateId()));
            licheckHistory.setCheckName(conventionalVendorliChecksToPerform.getCheckName());
            licheckHistory.setCheckUniqueId(conventionalVendorliChecksToPerform.getCheckUniqueId());
            licheckHistory.setCreatedBy(conventionalVendorliChecksToPerform.getCreatedBy().getUserName());
            licheckHistory.setCreatedOn(new Date());
            licheckHistory.setCheckStatus(conventionalVendorliChecksToPerform.getCheckStatus().getCheckStatusCode());
            licheckHistory.setCandidateStatus(conventionalVendorCandidatesSubmitted.getStatus().getStatusCode());
            licheckHistory.setRequestType(conventionalVendorCandidatesSubmitted.getRequestType());
            licheckHistory.setRequestId(Long.valueOf(conventionalVendorliChecksToPerform.getRequestId()));
            LicheckHistory save = licheckHistoryRepository.save(licheckHistory);
            log.info("licheck history  ON RESUMBITTED" + save.getId());

        }
        Boolean result = false;
        Date dateOfExpire = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dateOfExpire);
        c.add(Calendar.DATE, 3);
        c.add(Calendar.HOUR, 1);
        Date currentDatePlusOne = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Email email = new Email();
        email.setTitle(emailProperties.getDigiverifierEmailTitle());
        email.setContent(prepareContentForInsufficiencyChecks(conventionalVendorCandidatesSubmitted, conventionalVendorliChecksToPerforms));
        email.setSender(emailProperties.getDigiverifierEmailSenderId());
        email.setReceiver(receiver);
        email.setCopiedReceiver(copiedreceiver);
        try {
            result = send(email);
            if (result) {
                log.info("Verification Email sent successfully to Digiverifier Team");
            } else {
                log.info("Verification Email sent unsuccessful to Digiverifier Team");
            }
        } catch (UnsupportedEncodingException ue) {
            log.info("Sending Email FAILED, Please Check your Email", ue.getMessage());
            log.error(ue.getMessage());
        } catch (MessagingException me) {
            log.error("Sending Email FAILED, Please Check your Email", me.getMessage());
            log.error(me.getMessage());
        } catch (Exception e) {
            log.error("Exception occured", e.getMessage());
        }
        return result;
    }

    public Boolean sendEmail(String candidateid, String candidatename, String receiver, String copiedreceiver) throws UnsupportedEncodingException, MessagingException {
        Boolean result = false;
        Date dateOfExpire = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dateOfExpire);
        c.add(Calendar.DATE, 3);
        c.add(Calendar.HOUR, 1);
        Date currentDatePlusOne = c.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Email email = new Email();
        email.setTitle(emailProperties.getDigiverifierEmailTitle());
        email.setContent(prepareContent(candidatename, candidateid, dateFormat.format(currentDatePlusOne)));
        email.setSender(emailProperties.getDigiverifierEmailSenderId());
        email.setReceiver(receiver);
        email.setCopiedReceiver(copiedreceiver);
        try {
            result = send(email);
            if (result) {
                log.info("Verification Email sent successfully to candidate : " + candidatename + " with candidate id : " + candidateid);
            } else {
                log.info("Verification Email sent unsuccessful to candidate : " + candidatename + " with candidate id : " + candidateid);
            }
        } catch (UnsupportedEncodingException ue) {
            log.info("Sending Email FAILED, Please Check your Email", ue.getMessage());
            log.error(ue.getMessage());
        } catch (MessagingException me) {
            log.error("Sending Email FAILED, Please Check your Email", me.getMessage());
            log.error(me.getMessage());
        } catch (Exception e) {
            log.error("Exception occured", e.getMessage());
        }
        return result;
    }


    private String prepareContent(String candidateName, String candidateId, String currentDatePlusOne) {
        String emailContent = null;
        emailContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "a:link, a:visited {" +
                "  background-color: #36f439;" +
                "  color: white;" +
                "  padding: 15px 25px;" +
                "  text-align: center;" +
                "  text-decoration: none;" +
                "  display: inline-block;" +
                "}" +

                "a:hover, a:active {" +
                "  background-color: red;" +
                "}" +

                ".center {" +
                "  margin: auto;" +
                "  width: 90%;" +
                "  border: 0px solid #73AD21;" +
                "  text-align: left; " +
                "  padding: 20px;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"center\">" +
                "<p>Dear Admin,<br><br>" +
                "<p>LTM has added the following new candidates. Your action is required on these candidates.</p><br>" +
                "</div>" +
                "<table border=\"1\">" +
                "<tr>" +
                "<th>Agent Name</th>" +
                "<th>Candidate Name</th>" +
                "<th>Candidate ID</th>" +
                "<th>PS.No</th>" +
                "<th>Request Type</th>" +
                "<th>Request ID</th>" +
                "<th>Fast Track</th>" +
                "<th>Stop Check Received Date</th>" +
                "<th>Data Uploaded</th>" +
                "<th>Status</th>" +
                "</tr>" +
                "<!--  -->" +
                "</table>" +
                "</body>" +
                "</html>";
        return emailContent;
    }

    public Boolean send(Email email) throws MessagingException, UnsupportedEncodingException {
        Boolean result = false;
        if (StringUtils.isNotEmpty(email.getSender())) {
            if (StringUtils.isNotEmpty(email.getReceiver())) {
                Properties props = System.getProperties();
                props.put("mail.transport.protocol", emailProperties.getMailTransportProtocol());
                props.put("mail.smtp.port", emailProperties.getSmtpPort());
                props.put("mail.smtp.starttls.enable", emailProperties.getMailStarttlsEnabled());
                props.put("mail.smtp.auth", emailProperties.getMailSmtpAuth());
                Session session = Session.getDefaultInstance(props);

                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(email.getSender(), emailProperties.getDigiverifierSenderNickName()));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getReceiver()));
                if (!StringUtils.isBlank(email.getCopiedReceiver())) {
                    if (email.getCopiedReceiver().indexOf(";") > 0) {
                        String[] recipientList = email.getCopiedReceiver().split(";");
                        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
                        int counter = 0;
                        for (String recipient : recipientList) {
                            recipientAddress[counter] = new InternetAddress(recipient.trim());
                            counter++;
                        }
                        msg.setRecipients(Message.RecipientType.CC, recipientAddress);
                    } else if (email.getCopiedReceiver().indexOf(",") > 0) {
                        String[] recipientList = email.getCopiedReceiver().split(",");
                        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
                        int counter = 0;
                        for (String recipient : recipientList) {
                            recipientAddress[counter] = new InternetAddress(recipient.trim());
                            counter++;
                        }
                        msg.setRecipients(Message.RecipientType.CC, recipientAddress);
                    } else {
                        msg.setRecipients(Message.RecipientType.CC, email.getCopiedReceiver());
                    }

                }
                msg.setSubject(email.getTitle());
                msg.setContent(email.getContent(), emailProperties.getMailContentType());

                // msg.setHeader("X-SES-CONFIGURATION-SET", "ConfigSet");
                Transport transport = session.getTransport();
                try {
                    if (Objects.nonNull(email.getAttachmentFile())) {
                        Multipart multipart = new MimeMultipart(); //1
                        // Create the attachment part
                        BodyPart attachmentBodyPart = new MimeBodyPart(); //2
                        DataSource source = new FileDataSource(email.getAttachmentFile());
                        attachmentBodyPart.setDataHandler(new DataHandler(source));
                        attachmentBodyPart.setFileName(email.getAttachmentName()); // 2
                        multipart.addBodyPart(attachmentBodyPart); //3
                        // Create the HTML Part
                        BodyPart htmlBodyPart = new MimeBodyPart(); //4
                        htmlBodyPart.setContent(email.getContent(), "text/html"); //5
                        multipart.addBodyPart(htmlBodyPart); // 6
                        // Set the Multipart's to be the email's content
                        msg.setContent(multipart); //7
                    }

                    log.info("Trying to open the transport connection");
                    transport.connect(emailProperties.getSesHost(), emailProperties.getSesUsername(), emailProperties.getSesPassword());
                    transport.sendMessage(msg, msg.getAllRecipients());
                    result = true;
                } catch (MessagingException me) {
                    log.error("Message Format not Supported .." + me.getMessage());
                } catch (Exception ex) {
                    log.error("Message Format not Supported .." + ex.getMessage());
                } finally {
                    log.info("Closing the transport connection");
                    transport.close();
                }
            } else {
                log.error("Receiver Details not Set/Found");
                throw new MessagingException("Message failed RFC-2822 validation: Receiver Details not Set/Found");
            }
        } else {
            log.error("Sender Details not Set/Found");
            throw new MessagingException("Message failed RFC-2822 validation: Sender Details not Set/Found");
        }
        return result;
    }


    public Boolean sendRelationshipEmail(String candidateCode, String candidatename, String receiver) {
        Boolean result = false;
        Email email = new Email();
        email.setTitle(emailProperties.getDigiverifierEmailTitle());
        email.setContent(prepareRelationshipContent(candidatename, candidateCode));
        email.setSender(emailProperties.getDigiverifierEmailSenderId());
        email.setReceiver(receiver);
        try {
            result = send(email);
            if (!result) {
                log.error("Address Verification Email failed to send to candidate : " + candidatename + " with candidate id : " + candidateCode);
            }
        } catch (UnsupportedEncodingException ue) {
            log.info("Sending Email FAILED, Please Check your Email in sendRelationshipEmail-->", ue);
            log.error(ue.getMessage());
        } catch (MessagingException me) {
            log.error("Sending Email FAILED, Please Check your Email in sendRelationshipEmail-->", me);
            log.error(me.getMessage());
        } catch (Exception e) {
            log.error("Exception occured in sendRelationshipEmail-->", e);
        }
        return result;
    }

    private String prepareRelationshipContent(String candidateName, String candidateId) {
        String emailContent = null;
        emailContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
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
                + ".center {" + "  margin: auto;" + "width: 90%;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=" + "center" + ">"
                + "<p>Dear " + candidateName + " ,<br>"
                + "<br>"
                + "<p>Greetings from Digiverifier!!!</p>" +
                "<p>You have been invited to complete Digital Background Verification </p><br>" +

                "<h3>I)   Note:</h3>" +
                "1.	Security and Privacy are important and will remain secure.<br>" +
                "2.	Please provide consent to share your 26AS statement and or EPFO Service <br>" +
                "History using your ITR login and or EPFO login respectively. If you are a not<br>" +
                "registered user, Google ITR registration and or UAN registration respectively<br>" +
                "to register<br>" +
                "3. Your passwords are encrypted and will not be stored." +
                "4. Data is used for employment verification purpose only." +
                "<br>" +
                "<br>" +

                "<h3>II)   Pre-requisites for initiating the process:</h3>" +
                "1.	Keep Digilocker, ITR & EPFO login Username & Password handy, please do not share them with anyone.<br>" +
                "2.	Your verification cannot be performed without logging into Digilocker, ITR & EPFO portals.<br>" +

                "<h3>III)   Steps to follow:</h3>" +
                "1.	Click on Green Button - \"Start Verification Process\" here or at the end of this mail<br>" +
                "2.	Subbmit LO<br>" +
                "3.	Login to Digilocker & password<br>" +
                "4.	Login to ITR using PAN, password<br>" +
                "5.	Don’t skip the UAN authentication<br>" +
                "6.	Login to EPFO page using UAN, password and Captcha code.<br>" +
                "7.	<dl>Data Verification Step, check Employer Name and Tenure.<br>" +

                "<dt>a.		Ensure that you have entered all the companies that you have mentioned in the RESUME even if your employer has not registered<br>"
                + "with EPFO. Add employment history & save it and proceed else it’d be considered as undisclosed/ omission of facts in the verification process<br>"
                + "for upto 5 years of experience</dt><br>"

                + "<dt>b. 		Add all the companies that includes not filed under ITR/ EPFO/ Onsite companies/ Salary Paid in Cash as your Experience in Employment<br>"
                + "Validation Form.</dt><br>"

                + "<dt>c. 	If your previous employer’s Date of Exit/ LWD is “NOT AVAILABLE”, you may refer your relieving letter/ experience letter for LWD and fill <br>"
                + "accordingly.</dt>"
                + "</dl>"
                + "<br>"
                + "<br>"
                + "<p><a href='" + emailProperties.getDigiverifierRelationshipUrllink() + candidateId + "'>Start Verification Process</a></p>"
                + "</div>"
                + "</body>"
                + "</html>";
        return emailContent;
    }

}
