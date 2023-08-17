package com.cos.myFridge.config.jwt;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.util.UriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.myFridge.config.auth.PrincipalDetails;
import com.cos.myFridge.dto.LoginRequest;
import com.cos.myFridge.repository.UserRepository;
import com.cos.myFridge.service.TokenManager;
import com.cos.myFridge.service.CookieManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	private final AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	private CookieManager cookieManager = new CookieManager();
	private TokenManager tokenManager = new TokenManager();
	
	// Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
	// 인증 요청시에 실행되는 함수 => /login
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		System.out.println("JwtAuthenticationFilter : 진입");

		ObjectMapper om = new ObjectMapper();
		LoginRequest loginRequest = null;
		try {
			loginRequest = om.readValue(request.getInputStream(), LoginRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("JwtAuthenticationFilter : "+ loginRequest);
		
		// 유저네임 패스워드 토큰 생성
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUserId(), 
						loginRequest.getUserPw());
		
		System.out.println("JwtAuthenticationFilter : 토큰 생성 완료");
		
		Authentication authentication = 
				authenticationManager.authenticate(authenticationToken);
		PrincipalDetails principalDetailis = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("Authentication : "+principalDetailis.getUser().getUserName());
		return authentication;
	}

	// JWT Token 생성해서 response에 담아주기
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();
		
		cookieManager.setCookie(JwtProperties.ACCESS_COOKIE, tokenManager.generateAccessToken(principalDetailis), response, 3600*24*7);
		cookieManager.setCookie(JwtProperties.REFRESH_COOKIE, tokenManager.generateRefreshToken(principalDetailis), response, 3600*24*7);
	}	
}