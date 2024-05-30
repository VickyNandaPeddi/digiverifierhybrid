package com.aashdit.digiverifier.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {

	private String title;
	private String content;
	private String sender;
	private String receiver;
	private String attachmentName;
	private File attachmentFile;
	private String copiedReceiver;
	private String bccReceiver;
	
	public Email(String emailTitle, String emailContent, String sender, String receiver, String copiedReceiver, String bccReceiver) {
		this.title = emailTitle;
		this.content = emailContent;
		this.sender = sender;
		this.receiver = receiver;
		this.copiedReceiver = copiedReceiver;
		this.bccReceiver = bccReceiver;	
	}

	
	

}