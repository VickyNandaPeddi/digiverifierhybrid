package com.aashdit.digiverifier.epfo.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aashdit.digiverifier.config.candidate.dto.EpfoDto;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.epfo.dto.EpfoDataFromDetailsDto;
import com.aashdit.digiverifier.epfo.model.EpfoData;

@Repository
public interface EpfoDataRepository extends JpaRepository<EpfoData,Long> {
	
	List<EpfoData> findAllByCandidateCandidateCode(String candidateCode);
	
	@Query(value ="SELECT name FROM t_dgv_candidate_epfo where candidate_id = ?1", nativeQuery = true)
	List<EpfoDto> findAllByCandidateCandidateId(Long candidateId);
	EpfoData  findByCandidateCandidateCode(String getCandidateCode);
	EpfoData findFirstByCandidateCandidateId(Long candidateId);
	

	@Query(value = "SELECT e.uan, e.name, e.company, e.doj, e.doe FROM t_dgv_candidate_epfo e WHERE e.candidate_id = ?1 AND e.uan = ?2", nativeQuery = true)
	List<Object[]> findAllCandidateByCandidateIdAndUan(Long candidateId,String uan);
	
	@Query(value ="SELECT * FROM t_dgv_candidate_epfo WHERE candidate_id = ?1 AND uan = ?2", nativeQuery = true)
	List<EpfoData> findAllByCandidateIdAndUan(Long candidateId,String uan);
	
//	default List<EpfoDataFromDetailsDto> findDtoByCandidateIdAndUan(Long candidateId, String uan) {
//        List<Object[]> resultList = findAllCandidateByCandidateIdAndUan(candidateId, uan);
//        List<EpfoDataFromDetailsDto> dtoList = new ArrayList<>();
//
//        for (Object[] result : resultList) {
//            EpfoDataFromDetailsDto dto = new EpfoDataFromDetailsDto();
//            dto.setUan((String) result[0]);
//            dto.setName((String) result[1]);
//            dto.setCompany((String) result[2]);
//            dto.setDoe((Date) result[3]);
//            dto.setDoj((Date) result[4]);
//            // Set other columns based on the order in the result array
//            dtoList.add(dto);
//        }
//        
//        return dtoList;
//    }
	
//	@Query(value = "SELECT * FROM t_dgv_candidate_epfo e WHERE e.candidate_id = :candidateId AND e.uan = :Uan", nativeQuery = true)
//	List<EpfoDto> findAllCandidateByCandidateIdAndUan(@Param("candidateId") Long candidateId,@Param("Uan") String Uan);
//	
	
	@Query("FROM EpfoData rd where rd.candidate.candidateCode =:candidateCode and rd.memberId=:memberId")
	List<EpfoData> findAllByCandidateCandidateCodeAndMemberId(@Param("candidateCode") String candidateCode,@Param("memberId") String memberId);
	
}
