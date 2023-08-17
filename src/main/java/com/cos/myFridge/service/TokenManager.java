package com.cos.myFridge.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.cos.myFridge.config.auth.PrincipalDetails;
import com.cos.myFridge.config.jwt.JwtProperties;
import com.cos.myFridge.model.User;
import com.cos.myFridge.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenManager {
	@Autowired
	private UserRepository userRepository;
	
	private CookieManager cookieManager = new CookieManager();
	
    public void verifyAccessToken(String token, HttpServletRequest request, HttpServletResponse response) {
        if (token == null) {
        	log.info("Access-Token이 없습니다!");
        	return;
        }

        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token);
            String name = jwt.getClaim("username").asString();

            if (name != null) {
	            User user = userRepository.findByUserName(name);
	
	            // 스프링 시큐리티가 수행해주는 권한 처리를 위해 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장
	            PrincipalDetails principalDetails = new PrincipalDetails(user);
	            Authentication authentication = new UsernamePasswordAuthenticationToken(
	                    principalDetails,
	                    null,
	                    principalDetails.getAuthorities()
	            );
	
	            // 강제로 시큐리티의 세션에 접근하여 값 저장
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        }
            return;
            
        } catch (JWTVerificationException e) {
            // 토큰 서명 검증 실패
        	log.info("Access-Token이 만료되었습니다. Refresh-Token 검증 중...");
        	verifyRefreshToken(getRefreshToken(request), request, response);
        	return; // 갱신 토큰 유효성 검증 리턴
        }        
    }

    // 갱신 토큰 유효기간 확인 및 이름 체크
    public void verifyRefreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        if (refreshToken == null) {
        	log.info("Refresh-Token이 없습니다!");
        	return;
        }

        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(refreshToken);
            String id = jwt.getClaim("id").asString();
            String name = jwt.getClaim("username").asString();
            
            cookieManager.setCookie(JwtProperties.ACCESS_COOKIE, generateAccessToken(id, name), response, 3600*24*7);	// 생성한 토큰을 쿠키에 저장하여 전송
            cookieManager.setCookie(JwtProperties.REFRESH_COOKIE, generateRefreshToken(id, name), response, 3600*24*7);
            
            if (name != null) {
	            User user = userRepository.findByUserName(name);
	
	            // 스프링 시큐리티가 수행해주는 권한 처리를 위해 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장
	            PrincipalDetails principalDetails = new PrincipalDetails(user);
	            Authentication authentication = new UsernamePasswordAuthenticationToken(
	                    principalDetails,
	                    null,
	                    principalDetails.getAuthorities()
	            );
	
	            // 강제로 시큐리티의 세션에 접근하여 값 저장
	            SecurityContextHolder.getContext().setAuthentication(authentication);
	        }
            log.info("Access 및 Refresh-Token이 갱신되었습니다."); // 새로운 Access 및 Refresh 토큰 발급
            return;
        } catch (JWTVerificationException e) {
            // 토큰 서명 검증 실패
        	log.error("Refresh-Token 검증에 실패했습니다: {}", e.getMessage()); // 토큰이 유효하지 않음
        	cookieManager.deleteCookie(JwtProperties.ACCESS_COOKIE, response); // Access, Refresh-Token 제거
        	cookieManager.deleteCookie(JwtProperties.REFRESH_COOKIE, response);
        	return;
        }
    }

    public String getAccessToken(HttpServletRequest request) { // 쿠키에서 가져오기
    	return cookieManager.getCookie(JwtProperties.ACCESS_COOKIE, request);        
    }
    
    public String getRefreshToken(HttpServletRequest request) {
    	return cookieManager.getCookie(JwtProperties.REFRESH_COOKIE, request);        
    }

    public String generateAccessToken(PrincipalDetails principalDetails) {
    	return JWT.create()
				.withSubject(principalDetails.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.ACCESS_EXPIRATION_TIME))
				.withClaim("id", principalDetails.getUser().getUserId())
				.withClaim("username", principalDetails.getUser().getUserName())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }
    
    public String generateAccessToken(String id, String name) {
    	return JWT.create()
		.withSubject(name)
		.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.ACCESS_EXPIRATION_TIME))
		.withClaim("id", id)
		.withClaim("username", name)
		.sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }
    
    public String generateRefreshToken(PrincipalDetails principalDetails) {
    	return JWT.create()
				.withSubject(principalDetails.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.REFRESH_EXPIRATION_TIME))
				.withClaim("id", principalDetails.getUser().getUserId())
				.withClaim("username", principalDetails.getUser().getUserName())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }

	public String generateRefreshToken(String id, String name) {
    	return JWT.create()
    			.withSubject(name)
    			.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.REFRESH_EXPIRATION_TIME))
    			.withClaim("id", id)
    			.withClaim("username", name)
    			.sign(Algorithm.HMAC512(JwtProperties.SECRET));
    }
}