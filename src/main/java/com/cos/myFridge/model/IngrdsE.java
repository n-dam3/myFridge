package com.cos.myFridge.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity(name = "expired_ingredients")
@NoArgsConstructor
@AllArgsConstructor
public class IngrdsE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_id")
    private Long seqId;
    
    @Column(name = "ingredient_nm")
    private String ingredientNm;     // 식재료명

    @Column(name = "ingredient_num")
    private String ingredientNum;   // 개수
    
    @Column(name = "storage_mtd")
    private String storageMtd;   // 보관방법
    
    @Column(name = "storage_date")
    private String storageDate;   // 보관일

    @Column(name = "expiration_date")
    private String expirationDate;	// 보관기한
    
    @Column(name = "memo")
    private String memo;	// 메모
    
    @Column(name = "freshness")
    private String freshness;	// 신선도
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}