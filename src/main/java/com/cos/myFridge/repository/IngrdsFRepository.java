package com.cos.myFridge.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.myFridge.model.IngrdsF;

@Repository
public interface IngrdsFRepository extends JpaRepository<IngrdsF, String>{
	List<IngrdsF> findByUserUserId(String userId);
	
	@Transactional
	void deleteBySeqId(Long seqId);
}