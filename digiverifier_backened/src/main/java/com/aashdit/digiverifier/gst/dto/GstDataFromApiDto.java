package com.aashdit.digiverifier.gst.dto;

import java.util.Date;


import lombok.Data;

@Data
public class GstDataFromApiDto {

	private Long gstId;
	private String candidateCode;
	private String company;
	private String panNumber;
	private String gstNumber;
	private Date createdOn;
	private String image;
	private String status;
	private String color;
	private String colorHexCode;
}
