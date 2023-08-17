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
@Entity(name = "ingredients")
@NoArgsConstructor
@AllArgsConstructor
public class Ingrds {
	@Id
    @Column(name = "ingredient_id")
    private String ingrdId;
	
    @Column(name = "ingredient_nm")
    private String ingrdNm;	
}
