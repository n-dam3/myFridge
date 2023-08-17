package com.cos.myFridge.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cos.myFridge.config.jwt.JwtProperties;
import com.cos.myFridge.model.User;
import com.cos.myFridge.repository.UserRepository;
import com.cos.myFridge.service.CookieManager;

@Controller
public class LoginController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CookieManager cookieManager;
	
	@GetMapping("/")
	public String index() {
		return "/api/v1/setUserFavor";
	}
	
	@GetMapping("/login")
	public String login() {
		return "loginForm";
	}
	
    @GetMapping("api/v1/setUserFavor")
    public String setUserFavor(HttpServletRequest request) {
    	try {
	        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
	                .verify(cookieManager.getCookie("Access-Token", request));
	    	
	        String id = jwt.getClaim("id").asString();
	        if (userRepository.getById(id).getUserFavor() == null) {
	        	return "setUserFavor";
	        } else {
	        	return "/api/v1/user";
	        }
    	}  catch (TokenExpiredException e) {
    		return "redirect:/api/v1/setUserFavor";
		} catch (Exception e) {
			e.printStackTrace();
			return "404";
		}
    }
    
    @PostMapping("/api/v1/setUserFavor")
	public ResponseEntity<String> setUserFavor(@RequestBody Map<String, Object> input, HttpServletRequest request, Model model) throws Exception {
		try {
		    DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
		    		.verify(cookieManager.getCookie("Access-Token", request));
		    String id = jwt.getClaim("id").asString();
			
		    String userFavor = (String) input.get("userFavor");
		    
		    User user = userRepository.getById(id);
		    user.setUserFavor(userFavor);
		    
			userRepository.save(user);
			return ResponseEntity.ok("선호도 조사 결과를 저장했습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("통신 문제가 발생했습니다.");
		}
	}
}