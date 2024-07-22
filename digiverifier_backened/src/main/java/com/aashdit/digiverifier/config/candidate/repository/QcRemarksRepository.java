package com.aashdit.digiverifier.config.candidate.repository;

import com.aashdit.digiverifier.config.candidate.model.QcRemarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QcRemarksRepository extends JpaRepository<QcRemarks, Long> {

    public List<QcRemarks> findByCandidateId(Long candidateId);

    public List<QcRemarks> findByCandidateCode(String candidateId);
    @Query(value = "SELECT * FROM t_dgv_qcremarks where qc_remarks_id=?1 and candidate_id=?2",nativeQuery = true)
    public List<QcRemarks> findByQcremarksIdAndCandidateId(Long qcRemarksId,Long candidateId);
}
