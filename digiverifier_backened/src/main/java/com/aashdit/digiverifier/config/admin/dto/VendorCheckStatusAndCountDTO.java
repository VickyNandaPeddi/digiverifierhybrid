package com.aashdit.digiverifier.config.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorCheckStatusAndCountDTO {
	
	private String statusName;
	private String statusCode;
	private Integer count;

}
