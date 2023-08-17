package com.cos.myFridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FridgeIngrds {
	private Long seqId;
    private String ingredientNm;     // 식재료명
    private String ingredientNum;   // 개수
    private String storageMtd;   // 보관방법
    private String storageDate;   // 보관일
    private String expirationDate;	// 보관기한
    private String memo;	// 메모
    private String freshness;	// 신선도
}