package com.cos.myFridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendMenus {
	private String recipeId; // 레시피번호
	private String recipeNm; // 레시피명
	private String ckgMtd; // 방법별
	private String ckgSitu; // 상황별
	private String ckgType; // 종류별
	private String recipeIngrds; // 식재료명
}
