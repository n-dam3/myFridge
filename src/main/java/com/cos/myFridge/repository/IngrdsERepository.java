package com.cos.myFridge.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.myFridge.model.IngrdsE;

@Repository
public interface IngrdsERepository extends JpaRepository<IngrdsE, String>{
	List<IngrdsE> findByUserUserId(String userId);
	
	@Transactional
	void deleteBySeqId(Long seqId);
}