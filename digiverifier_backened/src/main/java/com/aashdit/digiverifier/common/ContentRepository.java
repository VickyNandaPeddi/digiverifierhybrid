package com.aashdit.digiverifier.common;

import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findAllByCandidateIdAndContentTypeIn(Long candidateId, List<ContentType> type);

    Optional<Content> findByCandidateIdAndContentTypeAndContentCategoryAndContentSubCategory(Long candidateId, ContentType contentType,
                                                                                             ContentCategory contentCategory, ContentSubCategory contentSubCategory);

//    @Query(value = "SELECT t1.* FROM t_dgv_content t1 WHERE t1.candidate_id =?1 AND t1.last_updated_on = (SELECT MAX(t2.last_updated_on) FROM t_dgv_content t2 WHERE t2.candidate_id = t1.candidate_id)", nativeQuery = true)
//    Content findByCandidateIdAndLastUpdatedOnMax(Long candidateId);

    Optional<Content> findByContentId(Long contentId);

    List<Content> findAllByCandidateId(Long candidateId);

    @Query("SELECT ct FROM Content ct WHERE ct.candidateId = ?1 AND ct.createdOn = (SELECT MAX(ct2.createdOn) FROM Content ct2 WHERE ct2.candidateId = ?1)")
    Content findByCandidateIdAndCreatedOn(Long candidateId);

}

