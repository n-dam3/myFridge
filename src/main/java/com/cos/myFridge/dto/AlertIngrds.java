package com.cos.myFridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertIngrds {
    private String ingredientNm;     // 식재료명
    private String date;	// 보관기한 - 오늘
    private String expirationDate;     // 보관기한
    private boolean dateFlg;  
}