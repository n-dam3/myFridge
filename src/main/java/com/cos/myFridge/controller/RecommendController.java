package com.cos.myFridge.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cos.myFridge.config.jwt.JwtProperties;
import com.cos.myFridge.dto.RecommendMenus;
import com.cos.myFridge.model.Recipes;
import com.cos.myFridge.model.User;
import com.cos.myFridge.repository.RecipesRepository;
import com.cos.myFridge.repository.UserRepository;
import com.cos.myFridge.service.CookieManager;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("api/v1")
public class RecommendController {
	@Autowired
    RecipesRepository recipesRepository;
	
	@Autowired
    UserRepository userRepository;
	
    @Autowired
    CookieManager cookieManager;
    
    @PostMapping("recommendMenu")
    public ResponseEntity <List<RecommendMenus>> recommendMenu(@RequestBody Map<String, Object> input, HttpServletRequest request) throws Exception {
    	try {
    	    DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
    	                .verify(cookieManager.getCookie("Access-Token", request));
    	    String id = jwt.getClaim("id").asString();
	
	    	String ingrds = (String) input.get("ingrds");
    	    String categories = userRepository.getById(id).getUserFavor();
    	    
    	    List<RecommendMenus> recommendMenus = new ArrayList<>();
	    	
	    	HttpPost httpPost = new HttpPost("http://localhost:5000/recommendMenu");
	    	List<BasicNameValuePair> nvps = new ArrayList<>();
	    	
	    	nvps.add(new BasicNameValuePair("categories", categories));
	    	nvps.add(new BasicNameValuePair("ingrds", ingrds));
	    	
	    	httpPost.setEntity(new UrlEncodedFormEntity(nvps, Charset.forName("UTF-8")));
	    	
	    	CloseableHttpClient httpclient = HttpClients.createDefault();
	    	CloseableHttpResponse response2 = httpclient.execute(httpPost);
	    	
	    	String recommendMenu = EntityUtils.toString(response2.getEntity(), Charset.forName("UTF-8"));
	    	String[] recommendMenuArray = recommendMenu.split(" ");
	    	List<String> recommendMenuList = Arrays.asList(recommendMenuArray);
	    	
	    	for (String rM : recommendMenuList) {
	    		Recipes recipes = recipesRepository.getById(rM);
	    		RecommendMenus r = new RecommendMenus(recipes.getRecipeId(), recipes.getRecipeNm(), recipes.getCkgMtd(), recipes.getCkgSitu(), recipes.getCkgType(), recipes.getRecipeIngrds());
	    		recommendMenus.add(r);
	    	}
	    	return new ResponseEntity<>(recommendMenus, HttpStatus.OK);
	    	
    	} catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
    }
    
    @PostMapping("jjim")
    public ResponseEntity <String> jjim(@RequestBody Map<String, Object> input, HttpServletRequest request) throws Exception {
    	try {
    	    DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
    	                .verify(cookieManager.getCookie("Access-Token", request));
    	    String id = jwt.getClaim("id").asString();
    	    
    	    Set<String> jjimSet = new HashSet<>();
    	    
    	    String[] recipeIds = ((String) input.get("recipeIds")).split(",");
    	    
    	    User user = userRepository.getById(id);
    	    
    	    if (user.getUserJjim() != null) {
	    	    String[] jjims = user.getUserJjim().split(",");
	            
	    	    for (String jjim : jjims) {
	            	jjimSet.add(jjim);
	            }
    	    }
    	    
            for (String recipeId : recipeIds) {
            	jjimSet.add(recipeId);
            }
            
    	    user.setUserJjim(String.join(",", jjimSet.toArray(new String[0])));
    	    userRepository.save(user);	
            return ResponseEntity.ok("찜 하기를 완료했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("통신 문제가 발생했습니다.");
        }
    }
}