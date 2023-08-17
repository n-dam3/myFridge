package com.cos.myFridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.myFridge.model.Ingrds;

@Repository
public interface IngrdsRepository extends JpaRepository<Ingrds, String>{
}