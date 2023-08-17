package com.cos.myFridge.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity(name = "recipes")
@NoArgsConstructor
@AllArgsConstructor
public class Recipes {
	@Id
    @Column(name = "recipe_id")
    private String recipeId;
	
	@Column(name = "recipe_nm")
    private String recipeNm;
	
    @Column(name = "ckg_mtd")
    private String ckgMtd;
    
    @Column(name = "ckg_situ")
    private String ckgSitu;
    
    @Column(name = "ckg_type")
    private String ckgType;
    
    @Column(name = "recipe_ingrds")
    private String recipeIngrds;
}