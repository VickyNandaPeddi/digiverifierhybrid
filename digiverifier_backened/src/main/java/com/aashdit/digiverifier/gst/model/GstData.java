package com.aashdit.digiverifier.gst.model;

import java.sql.Types;
import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.format.annotation.DateTimeFormat;

import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.superadmin.model.Color;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "t_dgv_candidate_gst_data")
public class GstData {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "gst_id")
		private Long gstId;
		
		@NotNull
		@ManyToOne
		@JoinColumn(name = "candidate_id")
		private Candidate candidate;
		
		@Column(name = "company")
		private String company;
		
		@Column(name = "pan_number")
		private String panNumber;
		
		@Column(name = "gst_number")
		private String gstNumber;
		
		@Column(name = "status")
		private String status;
		
		@DateTimeFormat(pattern = "dd/MM/yyyy")
		@Column(name = "created_on")
		private Date createdOn;
		
		@DateTimeFormat(pattern = "dd/MM/yyyy")
		@Column(name = "updated_on")
		private Date updatedOn;
		
		@ManyToOne
		@JoinColumn(name = "created_by")
		private User createdBy;
		
		@ManyToOne
		@JoinColumn(name = "last_updated_by")
		private User lastUpdatedBy;
		
		@JdbcTypeCode(Types.BINARY)
	    @Column(name = "image", columnDefinition="LONGBLOB")
	    private byte[] image;
		
		@ManyToOne
		@JoinColumn(name = "color")
		private Color color;

}
