/**
 * 
 */
package com.aashdit.digiverifier.config.admin.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@Table(name = "t_dgv_hybrid_convetional_criminal_check")
public class CriminalCheck {

	private static final long serialVersionUID = -763414907911681633L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column(name = "proceedings_type")
    private String proceedingsType;

    @Column(name = "date_of_search")
    private String dateOfSearch;

    @Column(name = "court")
    private String court;

    @Column(name = "jurisdiction")
    private String jurisdiction;

    @Column(name = "name_of_the_court")
    private String nameOfTheCourt;

    @Column(name = "result")
    private String result;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "vendor_check_id")
    private Long vendorCheckId;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "vendor_upload_check_id")
    private Long vendorUploadCheckId;
}
