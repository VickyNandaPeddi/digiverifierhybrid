package com.aashdit.digiverifier.config.candidate.repository;

import java.util.Date;
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

	//this is for Download
	@Query("SELECT e FROM UanSearchData e WHERE e.applicantId = :applicantId AND e.uan = :uan AND e.uanSearchId = :uanSearchId")
	List<UanSearchData> findByApplicantIdAndUanAndUanSearchId(@Param("applicantId") String applicantId, @Param("uan") String uan, @Param("uanSearchId") String uanSearchId);

	//this is for bulkDownload
	@Query("SELECT e FROM UanSearchData e WHERE e.bulkUanId = :bulkId AND e.uan = :uan")
	List<UanSearchData> findByBulkIdAndUan(@Param("bulkId") String bulkId, @Param("uan") String uan);

	
    List<UanSearchData> findByApplicantId(String applicantId);
    
    @Query("SELECT e FROM UanSearchData e WHERE  e.uan = :uan")
    List<UanSearchData> findByUan(@Param("uan") String uan);
    
    
    @Query("SELECT e FROM UanSearchData e WHERE e.bulkUanId = :bulkUanId")
    List<UanSearchData> findByBulkUanId(@Param("bulkUanId") String bulkUanId);
    
    @Query("SELECT e FROM UanSearchData e WHERE e.uploadedOn >= :from AND e.uploadedOn <= :to AND e.uploadedBy = :user")
    List<UanSearchData> uanSearchDashboardFilter(
        @Param("from") Date from,
        @Param("to") Date to,
        @Param("user") String user
    );
    
    //Retrive the data by uan,applicantId and bulkId
    @Query("SELECT e FROM UanSearchData e WHERE e.applicantId = :applicantId AND e.uan = :uan AND e.bulkUanId = :bulkId")
    UanSearchData findByBulkIdAndUanAndApplicantId(@Param("applicantId") String applicantId,@Param("uan") String uan,@Param("bulkId") String bulkId);

  //Retrive the data by pan,applicantId and bulkId
    @Query("SELECT e FROM UanSearchData e WHERE e.applicantId = :applicantId AND e.pan = :pan AND e.bulkUanId = :bulkId")
    UanSearchData findByBulkIdAndPanAndApplicantId(@Param("applicantId") String applicantId,@Param("pan") String pan,@Param("bulkId") String bulkId);
    
    //Retrive the data by pan,applicantId and candidateId
    @Query("SELECT e FROM UanSearchData e WHERE e.applicantId = :applicantId AND e.pan = :pan AND e.candidateId = :candidateId")
    UanSearchData findByCandidateIdAndPanAndApplicantId(@Param("applicantId") String applicantId,@Param("pan") String pan,@Param("candidateId") Long candidateId);


}
