/**
 * 
 */
package com.aashdit.digiverifier.config.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.admin.model.CriminalCheck;

@Repository
public interface CriminalCheckRepository extends JpaRepository<CriminalCheck,Long>{
	
	public List<CriminalCheck> findByCandidateId(Long candidateId);
    public List<CriminalCheck> findByCandidateIdAndProceedingsType(Long candidateId,String proceedingsType);

    public CriminalCheck findByVendorUploadCheckIdAndProceedingsTypeAndCourt(Long vendorUploadCheckId,String proceedingType,String court);


//    List<CriminalCheck> findByCheckUniqueId(String checkUniqueId);
    List<CriminalCheck> findByVendorCheckIdAndProceedingsType(Long vendorCheckId,String proceedingType);

}
