package com.aashdit.digiverifier.vendorcheck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_dgv_conventional_licheck_history")
public class LicheckHistory {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Id;

    private Long requestId;

    private Long candidateId;

    private String requestType;

    private Long checkUniqueId;

    private String checkStatus;

    private String CheckName;

    private Date createdOn;

    private String createdBy;

    private String candidateStatus;
}
