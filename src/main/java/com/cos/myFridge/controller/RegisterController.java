package com.cos.myFridge.controller;

import java.util.Base64;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cos.myFridge.model.User;
import com.cos.myFridge.repository.UserRepository;
import com.cos.myFridge.service.CookieManager;

@Controller
public class RegisterController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private CookieManager cookieManager;
	
	@GetMapping("/register")
	public String register() {
		return "registerForm";
	}
	
	@PostMapping("/registerControl")
	public String register(User user) {
		try {
			String rawPassword = user.getUserPw();
			String encPassword = bCryptPasswordEncoder.encode(rawPassword);
			user.setUserPw(encPassword);
			user.setUserRoles("ROLE_USER");
	
			System.out.println(user.toString());
	
			userRepository.save(user);
			return "loginForm";
		} catch (Exception e) {
			e.printStackTrace();
			return "404";
		}
	}

    @PostMapping("/checkNameDuplicateControl")
    public ResponseEntity<String> checkNameDuplicate(@RequestBody User inputName) {
        boolean isDuplicate = userRepository.existsByUserName(inputName.getUserName());
        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 닉네임입니다. 다른 닉네임을 사용해주세요.");
        } else {
            return ResponseEntity.ok("사용 가능한 닉네임입니다. 가입을 계속 진행해주세요.");
        }
    }

    @PostMapping("/checkEmailDuplicateControl")
    public ResponseEntity<String> checkEmailDuplicate(@RequestBody User inputId) {
        boolean isDuplicate = userRepository.existsById(inputId.getUserId());
        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 이메일 (아이디)입니다. 다른 이메일을 사용해주세요.");
        } else {
            return ResponseEntity.ok("사용 가능한 이메일 (아이디)입니다. 가입을 계속 진행해주세요.");
        }
    }
    
    @PostMapping("/checkEmailExistControl")
    public ResponseEntity<String> checkEmailExist(@RequestBody User inputId) {
        boolean isDuplicate = userRepository.existsById(inputId.getUserId());
        if (isDuplicate) {
            return ResponseEntity.ok("해당 이메일 (아이디)로 인증번호를 보냈습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 이메일 (아이디)은 가입되어있지 않습니다. 회원 가입을 진행해주세요.");
        }
    }

	@GetMapping("/findPw")
	public String findPassword() {
		return "findPw";
	}

	@GetMapping("/checkAN")
	public String checkAN() {
		return "checkAN";
	}
	
    @PostMapping("/checkANControl")
    public ResponseEntity<String> checkAN(@RequestBody Map<String, String> requestBody, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String authNum = requestBody.get("authNum");
        String[] authInfo;
        
        try {
            authInfo = new String(Base64.getDecoder().decode(cookieManager.getCookie("Auth-Info", request))).split("#", 2);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠키가 존재하지 않습니다.");
        }
                
        try {
            if (bCryptPasswordEncoder.matches(authNum, authInfo[1])) {
            	cookieManager.setCookie("Id-Info", Base64.getEncoder().encodeToString(authInfo[0].getBytes()), response);
                return ResponseEntity.ok("인증에 성공했습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("인증에 실패했습니다.");
            }
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("쿠키에 인증번호가 없습니다.");
        }
    }

	@GetMapping("/resetPw")
	public String resetPw() {
		return "resetPw";
	}
	
	@PostMapping("/resetPwControl")
	public ResponseEntity<String> resetPw(@RequestBody Map<String, String> requestBody, HttpServletRequest request, HttpServletResponse response) {
	    try {
	        String userPw = requestBody.get("userPw");
	        User user = userRepository.getById(new String(Base64.getDecoder().decode(cookieManager.getCookie("Id-Info", request))));
	    	
	        if (bCryptPasswordEncoder.matches(userPw, user.getUserPw())) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("이전 비밀번호와 같은 비밀번호입니다. 다른 비밀번호를 입력해 주세요.");
	        } else {
	            user.setUserPw(bCryptPasswordEncoder.encode(userPw));
	            userRepository.save(user);
	            cookieManager.deleteCookie("Id-Info", response);
	            return ResponseEntity.ok("비밀번호를 변경했습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류가 발생했습니다. 다시 시도해 주세요."); // Error가 발생하는 경우 1. 서버 통신 문제, 2. Id-Info가 없는 경우
	    }
	}
}