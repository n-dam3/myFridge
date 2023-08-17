package com.cos.myFridge.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.cos.myFridge.repository.UserRepository;
import com.cos.myFridge.service.TokenManager;

// 인가
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private UserRepository userRepository;
	private TokenManager tokenManager;
	
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, TokenManager tokenManager) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    	tokenManager.verifyAccessToken(tokenManager.getAccessToken(request), request, response);
	    chain.doFilter(request, response);
    }
}