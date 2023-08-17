package com.cos.myFridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.myFridge.model.Recipes;

@Repository
public interface RecipesRepository extends JpaRepository<Recipes, String>{
	
}