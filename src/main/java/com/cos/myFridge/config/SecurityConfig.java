package com.cos.myFridge.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.cos.myFridge.config.jwt.JwtAuthenticationFilter;
import com.cos.myFridge.config.jwt.JwtAuthorizationFilter;
import com.cos.myFridge.config.jwt.JwtProperties;
import com.cos.myFridge.config.oauth.OAuth2SuccessHandler;
import com.cos.myFridge.config.oauth.PrincipalOauth2UserService;
import com.cos.myFridge.repository.UserRepository;
import com.cos.myFridge.service.TokenManager;

@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
public class SecurityConfig {	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CorsConfig corsConfig;
	
	@Autowired
	private TokenManager tokenManager;
	
	@Autowired
	private OAuth2SuccessHandler successHandler;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		    .csrf().disable()
		    .formLogin().disable()
		    .httpBasic().disable();
		    
		http
		    .addFilter(corsConfig.corsFilter())
		    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and()
		    .apply(new MyCustomDsl()) // 커스텀 필터 등록
		    .and()
		    .logout()
		    .deleteCookies(JwtProperties.ACCESS_COOKIE, JwtProperties.REFRESH_COOKIE)
		    .and()
		    .authorizeRequests()
		        .antMatchers("/", "/api/v1/**")
		        .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
		        .antMatchers("/api/v1/manager/**")
		        .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
		        .antMatchers("/api/v1/admin/**")
		        .access("hasRole('ROLE_ADMIN')")
		        .anyRequest().permitAll()
		    .and()
		    .oauth2Login()
		        .loginPage("/login")
		        .userInfoEndpoint()
		            .userService(principalOauth2UserService)
		        .and()
		        .successHandler(successHandler);
				
		return http.build();
	}

	public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
			http
					.addFilter(new JwtAuthenticationFilter(authenticationManager))
					.addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository, tokenManager));
		}
	}
}