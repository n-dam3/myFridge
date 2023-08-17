package com.cos.myFridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyIngrds {
	private Long seqId;
    private String ingredientNm;    // 식재료명
    private String ingredientNum;   // 개수
    private String storageMtd1; 	// 보관방법1
    private String storageMtd2;		// 보관방법2
    private String storageMtd3;		// 보관방법3
    private String storageDate;   	// 보관일
    private String expirationDate;	// 보관기한
    private String memo;			// 메모
    private String freshness;		// 신선도
}
