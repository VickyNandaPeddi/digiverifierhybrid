package com.aashdit.digiverifier.epfo.dto;

import lombok.Data;
import java.util.Date;

@Data
public class EpfoDataFromDetailsDto {

	String uan;
	String name;
	String company;
	Date doe;
	Date doj;
	
}
