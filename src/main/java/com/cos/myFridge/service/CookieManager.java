package com.cos.myFridge.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service
public class CookieManager {
	public void setCookie(String cookieName, String value, HttpServletResponse response) {
	    Cookie cookie = new Cookie(cookieName, value);
	    cookie.setPath("/"); //모든 경로에서 접근 가능하도록 설정
	    cookie.setHttpOnly(true);
	    cookie.setSecure(true);
	    	    
	    response.addCookie(cookie); // response에 Cookie 추가
	}
	
	public void setCookie(String cookieName, String value, HttpServletResponse response, int setMaxAge) {
	    Cookie cookie = new Cookie(cookieName, value);
	    cookie.setMaxAge(setMaxAge);
	    cookie.setPath("/"); //모든 경로에서 접근 가능하도록 설정
	    cookie.setHttpOnly(true);
	    cookie.setSecure(true);
	    	    
	    response.addCookie(cookie); // response에 Cookie 추가
	}
	
	public String getCookie(String cookieName, HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies(); // 모든 쿠키 가져오기
	    if(cookies != null){
	        for (Cookie c : cookies) {
	            String name = c.getName(); // 쿠키 이름 가져오기
	            String value = c.getValue(); // 쿠키 값 가져오기
	            if (name.equals(cookieName)) {
	                return value;
	            }
	        }
	    }
	    return null;
	}

	public void deleteCookie(String cookieName, HttpServletResponse response) {
	    Cookie cookie = new Cookie(cookieName, null); // 삭제할 쿠키에 대한 값을 null로 지정
	    cookie.setMaxAge(0); // 유효시간을 0으로 설정해서 바로 만료시킨다.
	    response.addCookie(cookie);
	}
	
	public void deleteAllCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies(); // 모든 쿠키의 정보를 cookies에 저장
		if (cookies != null) { // 쿠키가 한개라도 있으면 실행
			for (int i = 0; i < cookies.length; i++) {
				cookies[i].setMaxAge(0); // 유효시간을 0으로 설정
				response.addCookie(cookies[i]); // 응답에 추가하여 만료시키기.
			}
		}
	}
}