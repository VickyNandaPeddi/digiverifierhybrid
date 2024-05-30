package com.aashdit.digiverifier.common;

import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content,Long> {
	List<Content> findAllByCandidateIdAndContentTypeIn(Long candidateId, List<ContentType> type);
	
	Optional<Content> findByCandidateIdAndContentTypeAndContentCategoryAndContentSubCategory(Long candidateId, ContentType contentType,
		ContentCategory contentCategory, ContentSubCategory contentSubCategory);
	
	Optional<Content> findByContentId(Long contentId);
	
	List<Content> findAllByCandidateId(Long candidateId);
	
	List<Content> findByCandidateIdAndContentSubCategory(Long candidateId, ContentSubCategory contentSubCategory);
	
	List<Content> findAllByCandidateIdAndContentTypeAndContentCategoryAndContentSubCategory(Long candidateId, ContentType contentType,
			ContentCategory contentCategory, ContentSubCategory contentSubCategory);
}

