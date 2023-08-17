package com.cos.myFridge.config.oauth;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.myFridge.config.auth.PrincipalDetails;
import com.cos.myFridge.config.oauth.provider.FaceBookUserInfo;
import com.cos.myFridge.config.oauth.provider.GoogleUserInfo;
import com.cos.myFridge.config.oauth.provider.OAuth2UserInfo;
import com.cos.myFridge.model.User;
import com.cos.myFridge.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// userRequest는 code를 받아서 accessToken을 응답 받은 객체
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest); // 회원 프로필 조회
		return processOAuth2User(userRequest, oAuth2User);
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
		// Attribute를 파싱해서 공통 객체로 묶음.
		OAuth2UserInfo oAuth2UserInfo = null;
		if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		} else {
			oAuth2UserInfo = new FaceBookUserInfo(oAuth2User.getAttributes());
		}

		User user = userRepository.findByUserId(oAuth2UserInfo.getEmail());
		
		if (user == null) {
			user = User.builder()
					.userName(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId())
					.userPw(bCryptPasswordEncoder.encode(UUID.randomUUID().toString()))
					.userId(oAuth2UserInfo.getEmail())
					.userRoles("ROLE_USER")
					.build();
			userRepository.save(user);
		}

		System.out.println(user.toString());
		
		return new PrincipalDetails(user, oAuth2User.getAttributes());
	}
}