package com.aashdit.digiverifier.config.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aashdit.digiverifier.config.admin.model.Token;
import java.util.Optional;


public interface TokenRepository extends JpaRepository<Token, Long>{
	
	@Query("select t from Token t where t.user.userId=:userId and (t.expired=false or t.revoked=false)")
	List<Token> findAllValidTokensByUser(Long userId);

	Optional<Token> findByUserToken(String userToken);
	
    @Query("SELECT t FROM Token t WHERE t.user.userId = :userId")
    List<Token> findByUserId(Long userId);
}
