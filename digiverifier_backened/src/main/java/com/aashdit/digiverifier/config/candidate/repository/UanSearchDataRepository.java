package com.aashdit.digiverifier.config.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.config.candidate.dto.BulkUanDTO;
import com.aashdit.digiverifier.config.candidate.dto.UanSearchDataDTO;
import com.aashdit.digiverifier.config.candidate.model.UanSearchData;


public interface UanSearchDataRepository extends JpaRepository<UanSearchData, Long>{
	
	@Query("SELECT e FROM UanSearchData e WHERE e.applicantId = :applicantId AND e.uan = :uan")
    List<UanSearchData> findByApplicantIdAndUan(@Param("applicantId") String applicantId, @Param("uan") String uan);

    List<UanSearchData> findByApplicantId(String applicantId);
    
    @Query("SELECT e FROM UanSearchData e WHERE  e.uan = :uan")
    List<UanSearchData> findByUan(@Param("uan") String uan);
    
    
    @Query("SELECT e FROM UanSearchData e WHERE e.bulkUanId = :bulkUanId")
    List<UanSearchData> findByBulkUanId(@Param("bulkUanId") String bulkUanId);
    
   

    


}
