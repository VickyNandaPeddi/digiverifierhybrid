package com.aashdit.digiverifier.config.candidate.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;


import lombok.Data;

@Data
@Entity
@Table(name = "t_dgv_uan_search_data")
public class UanSearchData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "uan_search_data_id")
	private Long uanSearchId;
	
	@Column(name = "applicant_id")
	private String applicantId;
	
	@Column(name = "uan")
	private String uan;
	
	@Column(name = "uploaded_by")
	private String uploadedBy;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(name = "uploaded_on")
	private Date uploadedOn;
	
	@Column(name = "total_record_uploaded")
	private int totalRecordUploaded;
	
	@Column(name = "total_record_fetched")
	private int totalRecordFetched;
	
	@Column(name = "total_record_failed")
	private int totalRecordFailed;
	
	@Column(name = "epfo_response",columnDefinition = "MEDIUMTEXT")
	private String EPFOResponse;
	
	@Column(name = "bulk_uan_id")
	private String bulkUanId;
	
	@Column(name = "pan")
	private String pan;
	
	@Column(name = "candidate_id")
	private Long candidateId;

}
