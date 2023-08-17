package com.cos.myFridge.config.jwt;

public interface JwtProperties {
	String SECRET = "403edf91f33d7a4961f56dd3d9f3f3c4";
	int ACCESS_EXPIRATION_TIME = 1800000; // 30분 (1/1000초)
	int REFRESH_EXPIRATION_TIME = 604800000; // 7일 (1/1000초)
	String TOKEN_PREFIX = "Bearer ";
	String ACCESS_COOKIE = "Access-Token";
	String REFRESH_COOKIE = "Refresh-Token";
}