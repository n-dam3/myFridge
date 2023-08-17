package com.cos.myFridge.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;     // 아이디(= 이메일)

    @Column(name = "user_pw")
    private String userPw;     // 비밀번호

    @Column(name = "user_name")
    private String userName;   // 닉네임
    
    @Column(name = "user_roles")
    private String userRoles;   // USER, ADMIN, MANAGER
    
    public List<String> getUserRolesList(){
    	if(this.userRoles.length() > 0) {
    		return Arrays.asList(this.userRoles.split(","));
    	}
    	return new ArrayList<>();
    }
    
    @Column(name = "user_favor")
    private String userFavor;   // 선호 태그

    @Column(name = "user_jjim")
    private String userJjim;	// 찜
}