package com.aashdit.digiverifier.epfo.remittance.model;

import java.sql.Types;
import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "t_dgv_candidate_remittance")
public class RemittanceData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "remittance_id")
	private Long remittanceId;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "candidate_id")
	private Candidate candidate;
	
	
	@Column(name = "name")
	String name;
	
	@Column(name = "company")
	String company;
	
	@Column(name = "member_id")
	String memberId;
	
	@Column(name = "year")
	String year;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(name = "created_on")
	Date createdOn;
	
	@JdbcTypeCode(Types.BINARY)
    @Column(name = "image", columnDefinition="LONGBLOB")
    private byte[] image;
	
	@ManyToOne
	@JoinColumn(name = "color")
	private Color color;
	
	@Column(name = "remark", length=2000)
	String remark;
}
