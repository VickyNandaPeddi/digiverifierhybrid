/**
* 
*/
package com.aashdit.digiverifier.config.admin.model;

 

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

 

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

 

import com.aashdit.digiverifier.config.superadmin.model.Source;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

 

import lombok.Data;

 

/**
* @author ${ashwani}
*
*/

 

@Data
@Entity
@Table(name = "t_dgv_conventional_attributes_master")
public class ConventionalAttributesMaster implements Serializable {

private static final long serialVersionUID = 3513694544081413484L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conventional_attributes_id")
    private Long checkId;

//    @Column(name = "check_name")
//    private String checkName;

    @Column(name = "agent_attribute_list", columnDefinition = "BLOB")
    private ArrayList<String> agentAttributeList;

    @Column(name = "vendor_attribute_list", columnDefinition = "BLOB")
    private ArrayList<String> vendorAttributeList; 

    @ManyToOne
    @JoinColumn(name = "source_id")
    private Source source;
    
    @Column(name = "global_check_type")
    private String globalCheckType;



 

}