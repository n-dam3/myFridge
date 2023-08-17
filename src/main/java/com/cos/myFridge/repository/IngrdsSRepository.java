package com.cos.myFridge.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.myFridge.model.IngrdsS;

@Repository
public interface IngrdsSRepository extends JpaRepository<IngrdsS, String>{
	List<IngrdsS> findByUserUserId(String userId);
	
	@Transactional
	void deleteBySeqId(Long seqId);
}