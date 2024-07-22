package com.aashdit.digiverifier.config.candidate.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Table(name = "t_dgv_qc_remarks")
@Entity
public class QcRemarks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qc_remarks_id")
    private Long qcRemarksId;


    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "candidate_code")
    private String  candidateCode;

    @Lob
    @Column(name = "qc_remarks")
    private String qcRemarks;


    @Column(name = "created_on")
    private Date createdOn;


    @Column(name = "last_updated_on")
    private Date lastUpdatedOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
}
