package com.cos.myFridge.config.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.cos.myFridge.config.jwt.JwtProperties;
import com.cos.myFridge.repository.UserRepository;
import com.cos.myFridge.service.CookieManager;
import com.cos.myFridge.service.TokenManager;

import com.cos.myFridge.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final TokenManager tokenManager;
	private final CookieManager cookieManager;
	private final UserRepository userRepository;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        User user = userRepository.findByUserId(oAuth2User.getName());
        
        log.info("토큰 발행 시작");
        
        cookieManager.setCookie(JwtProperties.ACCESS_COOKIE, tokenManager.generateAccessToken(user.getUserId(), user.getUserName()), response, 3600*24*7);
		cookieManager.setCookie(JwtProperties.REFRESH_COOKIE, tokenManager.generateRefreshToken(user.getUserId(), user.getUserName()), response, 3600*24*7);
		
		String targetUrl = UriComponentsBuilder.fromUriString("/").build().toUriString();
		
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
