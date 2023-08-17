package com.cos.myFridge.controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.cos.myFridge.config.jwt.JwtProperties;
import com.cos.myFridge.dto.AlertIngrds;
import com.cos.myFridge.dto.FridgeIngrds;
import com.cos.myFridge.dto.ModifyIngrds;
import com.cos.myFridge.dto.RecommendMenus;
import com.cos.myFridge.model.Ingrds;
import com.cos.myFridge.model.IngrdsE;
import com.cos.myFridge.model.IngrdsF;
import com.cos.myFridge.model.IngrdsS;
import com.cos.myFridge.model.User;
import com.cos.myFridge.model.Recipes;
import com.cos.myFridge.repository.IngrdsFRepository;
import com.cos.myFridge.repository.IngrdsRepository;
import com.cos.myFridge.repository.IngrdsSRepository;
import com.cos.myFridge.repository.RecipesRepository;
import com.cos.myFridge.repository.IngrdsERepository;
import com.cos.myFridge.repository.UserRepository;
import com.cos.myFridge.service.CookieManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class IndexController {
    private final UserRepository userRepository;
    private final IngrdsRepository ingrdsRepository;
    private final IngrdsFRepository ingrdsFRepository;
    private final IngrdsSRepository ingrdsSRepository;
    private final IngrdsERepository ingrdsERepository;
    private final RecipesRepository recipesRepository;

    @Autowired
    private CookieManager cookieManager;
    
    @GetMapping("user")
    public String index(HttpServletRequest request, Model model) {
    	try {
	        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
	                .verify(cookieManager.getCookie("Access-Token", request));
	    	
	        String id = jwt.getClaim("id").asString();
	        String userName = jwt.getClaim("username").asString();
	
	        List<IngrdsF> ingrdsF = ingrdsFRepository.findByUserUserId(id);
	        List<IngrdsS> ingrdsS = ingrdsSRepository.findByUserUserId(id);
	        List<IngrdsE> ingrdsE = ingrdsERepository.findByUserUserId(id);
	
	        List<FridgeIngrds> ingrds = new ArrayList<>();
	        List<AlertIngrds> ingrdsAlert = new ArrayList<>();
	
	        for (IngrdsF f : ingrdsF) {
	            FridgeIngrds dto = new FridgeIngrds(f.getSeqId(), f.getIngredientNm(), f.getIngredientNum(),
	                    f.getStorageMtd(), f.getStorageDate(), f.getExpirationDate().substring(0, 10), f.getMemo(),
	                    f.getFreshness());
	            ingrds.add(dto);
	        }
	
	        for (IngrdsS s : ingrdsS) {
	            FridgeIngrds ingrdsDTO = new FridgeIngrds(
	                s.getSeqId(), s.getIngredientNm(), s.getIngredientNum(),
	                s.getStorageMtd(), s.getStorageDate(), s.getExpirationDate().substring(0, 10),
	                s.getMemo(), s.getFreshness()
	            );
	            ingrds.add(ingrdsDTO);

	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(s.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    s.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    s.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            }
	        }
	
	        for (IngrdsE e : ingrdsE) {
	            FridgeIngrds ingrdsDTO = new FridgeIngrds(e.getSeqId(), e.getIngredientNm(), e.getIngredientNum(),
	                    e.getStorageMtd(), e.getStorageDate(), e.getExpirationDate().substring(0, 10), e.getMemo(),
	                    e.getFreshness());
	            ingrds.add(ingrdsDTO);
	            
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(e.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    e.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    e.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            }
	        }
	        
	        model.addAttribute("userName", userName);
	        model.addAttribute("ingrds", ingrds);
	        model.addAttribute("ingrdsAlert", ingrdsAlert);
	        model.addAttribute("ingrdsAlertSize", ingrdsAlert.size());
	
	        return "index";
    	}  catch (TokenExpiredException e) {
    		return "redirect:/api/v1/user";
		} catch (Exception e) {
			e.printStackTrace();
			return "404";
		}
    }

    @GetMapping("jjim")
    public String jjim(HttpServletRequest request, Model model) throws Exception {
    	try {
	        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
	                .verify(cookieManager.getCookie("Access-Token", request));
	    	
	        String id = jwt.getClaim("id").asString();
	        String userName = jwt.getClaim("username").asString();
	
	        List<IngrdsS> ingrdsS = ingrdsSRepository.findByUserUserId(id);
	        List<IngrdsE> ingrdsE = ingrdsERepository.findByUserUserId(id);
	
	        List<AlertIngrds> ingrdsAlert = new ArrayList<>();
	        for (IngrdsS s : ingrdsS) {
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(s.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    s.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    s.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            }
	        }
	
	        for (IngrdsE e : ingrdsE) {
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(e.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    e.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    e.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            }
	        }
	        
	        model.addAttribute("userName", userName);
	        model.addAttribute("ingrdsAlert", ingrdsAlert);
	        model.addAttribute("ingrdsAlertSize", ingrdsAlert.size());
	
	        return "jjim";
    	}  catch (TokenExpiredException e) {
    		return "redirect:/api/v1/jjim";
		} catch (Exception e) {
			e.printStackTrace();
			return "404";
		}
    }
    
    @GetMapping("loadJjim")
    public ResponseEntity <List<RecommendMenus>> jjim(HttpServletRequest request) throws Exception {
    	try {
    	    DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
    	                .verify(cookieManager.getCookie("Access-Token", request));
    	    String id = jwt.getClaim("id").asString();
    	    User user = userRepository.getById(id);
    	    
    	    List<RecommendMenus> jjimList = new ArrayList<>();
    	    String[] jjims = user.getUserJjim().split(",");
    	    
    	    for (String j : jjims) {
    	    	Recipes recipes = recipesRepository.getById(j);
	    		RecommendMenus r = new RecommendMenus(recipes.getRecipeId(), recipes.getRecipeNm(), recipes.getCkgMtd(), recipes.getCkgSitu(), recipes.getCkgType(), recipes.getRecipeIngrds());
    	    	jjimList.add(r);
            }
    	    return new ResponseEntity<>(jjimList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
	        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }    
    
    @GetMapping("loadIngrds")
    public ResponseEntity<List<String>> loadIngrds() throws Exception {
    	List<Ingrds> ingrds = (List<Ingrds>) ingrdsRepository.findAll();
        List<String> ingrdList = new ArrayList<>();
        
        for (Ingrds ingrd : ingrds) {
        	ingrdList.add(ingrd.getIngrdNm());
        }
        
        return new ResponseEntity<>(ingrdList, HttpStatus.OK); // 일반 List<String>으로 보내면 홈페이지로 매핑돼 404 에러 발생
    }

    @GetMapping("caution")
    public String caution(HttpServletRequest request, Model model) throws Exception {
    	try {
	        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
	                .verify(cookieManager.getCookie("Access-Token", request));
	        String id = jwt.getClaim("id").asString();
	        String userName = jwt.getClaim("username").asString();
	
	        List<IngrdsS> ingrdsS = ingrdsSRepository.findByUserUserId(id);
	        List<IngrdsE> ingrdsE = ingrdsERepository.findByUserUserId(id);
	
	        List<FridgeIngrds> ingrds = new ArrayList<>();
	        List<AlertIngrds> ingrdsAlert = new ArrayList<>();
	
	        for (IngrdsS s : ingrdsS) {
	            FridgeIngrds ingrdsDTO = new FridgeIngrds(s.getSeqId(), s.getIngredientNm(), s.getIngredientNum(),
	                    s.getStorageMtd(), s.getStorageDate(), s.getExpirationDate().substring(0, 10), s.getMemo(),
	                    s.getFreshness());
	            ingrds.add(ingrdsDTO);
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(s.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    s.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    s.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            }
	        }
	
	        for (IngrdsE e : ingrdsE) {
	            FridgeIngrds ingrdsDTO = new FridgeIngrds(e.getSeqId(), e.getIngredientNm(), e.getIngredientNum(),
	                    e.getStorageMtd(), e.getStorageDate(), e.getExpirationDate().substring(0, 10), e.getMemo(),
	                    e.getFreshness());
	            ingrds.add(ingrdsDTO);
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(e.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    e.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    e.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            }
	        }
	
	        model.addAttribute("userName", userName);
	        model.addAttribute("ingrds", ingrds);
	        model.addAttribute("ingrdsAlert", ingrdsAlert);
	        model.addAttribute("ingrdsAlertSize", ingrdsAlert.size());
	
	        return "caution";
    	} catch (TokenExpiredException e) {
    		return "redirect:/api/v1/caution";
		} catch (Exception e) {
			e.printStackTrace();
			return "404";
		}
    }
    
    @GetMapping("recommendMenu")
    public String recommendMenu(HttpServletRequest request, Model model) throws Exception {
    	try {
	        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
	                .verify(cookieManager.getCookie("Access-Token", request));
	        String id = jwt.getClaim("id").asString();
	        String userName = jwt.getClaim("username").asString();
	
	        List<IngrdsS> ingrdsS = ingrdsSRepository.findByUserUserId(id);
	        List<IngrdsE> ingrdsE = ingrdsERepository.findByUserUserId(id);
	
	        List<AlertIngrds> ingrdsAlert = new ArrayList<>();
	
	        for (IngrdsS s : ingrdsS) {
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(s.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    s.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    s.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            }
	        }
	
	        for (IngrdsE e : ingrdsE) {
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(e.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    e.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    e.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            }
	        }
	
	        model.addAttribute("userName", userName);
	        model.addAttribute("ingrdsAlert", ingrdsAlert);
	        model.addAttribute("ingrdsAlertSize", ingrdsAlert.size());
	
	        return "recommendMenu";
    	} catch (TokenExpiredException e) {
    		return "redirect:/api/v1/recommendMenu";
		} catch (Exception e) {
			e.printStackTrace();
			return "404";
		}
    }
    
    @GetMapping("insertIngrds")
    public String insertIngrds(HttpServletRequest request, Model model) throws Exception {
    	try {
	        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
	                .verify(cookieManager.getCookie("Access-Token", request));
	        String id = jwt.getClaim("id").asString();
	        String userName = jwt.getClaim("username").asString();
	        
	        List<IngrdsS> ingrdsS = ingrdsSRepository.findByUserUserId(id);
	        List<IngrdsE> ingrdsE = ingrdsERepository.findByUserUserId(id);
	
	        List<AlertIngrds> ingrdsAlert = new ArrayList<>();
	
	        for (IngrdsS s : ingrdsS) {
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(s.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    s.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    s.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            } else {
	                break;
	            }
	        }
	
	        for (IngrdsE e : ingrdsE) {
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(e.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    e.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    e.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            } else {
	                break;
	            }
	        }
	        
	        model.addAttribute("userName", userName);
	        model.addAttribute("ingrdsAlert", ingrdsAlert);
	        model.addAttribute("ingrdsAlertSize", ingrdsAlert.size());
	        return "insertIngrds";
    	} catch (TokenExpiredException e) {
    		return "redirect:/api/v1/insertIngrds";
		} catch (Exception e) {
			e.printStackTrace();
			return "404";
		}
    }
    
    @PostMapping("insertIngrds")
    public ResponseEntity<String> insertIngrds(@RequestBody List<FridgeIngrds> fridgeIngrds, HttpServletRequest request, Model model) throws Exception {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
                    .verify(cookieManager.getCookie("Access-Token", request));
            String id = jwt.getClaim("id").asString();

            User user = userRepository.findByUserId(id);
            for (FridgeIngrds ingrd : fridgeIngrds) {
                System.out.println(ingrd.toString());

                switch (ingrd.getFreshness()) {
                    case "F":
                        try {
                            IngrdsF ingrdsF = new IngrdsF();
                            ingrdsF.setIngredientNm(ingrd.getIngredientNm());
                            ingrdsF.setIngredientNum(ingrd.getIngredientNum());
                            ingrdsF.setStorageMtd(ingrd.getStorageMtd());
                            ingrdsF.setStorageDate(ingrd.getStorageDate());
                            ingrdsF.setExpirationDate(ingrd.getExpirationDate());
                            ingrdsF.setMemo(ingrd.getMemo());
                            ingrdsF.setFreshness(ingrd.getFreshness());
                            ingrdsF.setUser(user);
                            ingrdsFRepository.save(ingrdsF);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("식재료 정보 추가에 실패했습니다. 추가한 식재료 정보를 다시 확인해 주세요.");
                        }
                        break;
                    case "S":
                        try {
                            IngrdsS ingrdsS = new IngrdsS();
                            ingrdsS.setIngredientNm(ingrd.getIngredientNm());
                            ingrdsS.setIngredientNum(ingrd.getIngredientNum());
                            ingrdsS.setStorageMtd(ingrd.getStorageMtd());
                            ingrdsS.setStorageDate(ingrd.getStorageDate());
                            ingrdsS.setExpirationDate(ingrd.getExpirationDate());
                            ingrdsS.setMemo(ingrd.getMemo());
                            ingrdsS.setFreshness(ingrd.getFreshness());
                            ingrdsS.setUser(user);
                            ingrdsSRepository.save(ingrdsS);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("식재료 정보 추가에 실패했습니다. 추가한 식재료 정보를 다시 확인해 주세요.");
                        }
                        break;
                    case "E":
                        try {
                            IngrdsE ingrdsE = new IngrdsE();
                            ingrdsE.setIngredientNm(ingrd.getIngredientNm());
                            ingrdsE.setIngredientNum(ingrd.getIngredientNum());
                            ingrdsE.setStorageMtd(ingrd.getStorageMtd());
                            ingrdsE.setStorageDate(ingrd.getStorageDate());
                            ingrdsE.setExpirationDate(ingrd.getExpirationDate());
                            ingrdsE.setMemo(ingrd.getMemo());
                            ingrdsE.setFreshness(ingrd.getFreshness());
                            ingrdsE.setUser(user);
                            ingrdsERepository.save(ingrdsE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("식재료 정보 추가에 실패했습니다. 추가한 식재료 정보를 다시 확인해 주세요.");
                        }
                        break;
                }
            }
            return ResponseEntity.ok("식재료 정보 추가를 완료했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("통신 문제가 발생했습니다.");
        }
    }

    @GetMapping("modifyIngrds")
    public String modifyIngrds(HttpServletRequest request, Model model) throws Exception {
    	try {
	        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
	                .verify(cookieManager.getCookie("Access-Token", request));
	        String id = jwt.getClaim("id").asString();
	        String userName = jwt.getClaim("username").asString();
	
	        List<IngrdsS> ingrdsS = ingrdsSRepository.findByUserUserId(id);
	        List<IngrdsE> ingrdsE = ingrdsERepository.findByUserUserId(id);
	
	        List<AlertIngrds> ingrdsAlert = new ArrayList<>();
	
	        for (IngrdsS s : ingrdsS) {
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(s.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    s.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    s.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            } else {
	                break;
	            }
	        }
	
	        for (IngrdsE e : ingrdsE) {
	            if (ingrdsAlert.size() <= 5) {
	                LocalDate expirationDate = LocalDate.parse(e.getExpirationDate().substring(0, 10));
	                boolean dateFlg = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate) > 0;

	                AlertIngrds alertDTO = new AlertIngrds(
	                    e.getIngredientNm(),
	                    String.valueOf(Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), expirationDate))),
	                    e.getExpirationDate().substring(0, 10),
	                    dateFlg
	                );
	                ingrdsAlert.add(alertDTO);
	            } else {
	                break;
	            }
	        }
	
	        String data = (String) request.getSession().getAttribute("data");
	        
	        if (data != null) {
	            try {
	                ObjectMapper objectMapper = new ObjectMapper();
	                List<FridgeIngrds> fridgeIngrds = objectMapper.readValue(data, new TypeReference<List<FridgeIngrds>>() {});
	                List<ModifyIngrds> modifyIngrds = new ArrayList<>();
	                for (FridgeIngrds f : fridgeIngrds) {
	                	ModifyIngrds m = new ModifyIngrds(f.getSeqId(), f.getIngredientNm(), f.getIngredientNum(), f.getStorageMtd(), "", "", f.getStorageDate(), f.getExpirationDate(), f.getMemo(), f.getFreshness());
	                	switch(f.getStorageMtd()) {
	                		case "냉동" :
	                			m.setStorageMtd2("냉장");
	                			m.setStorageMtd3("실온");
	                			break;
	                		case "냉장" :
	                			m.setStorageMtd2("실온");
	                			m.setStorageMtd3("냉동");
	                			break;
	                		case "실온" :
	                			m.setStorageMtd2("냉동");
	                			m.setStorageMtd3("냉장");
	                			break;
	                	}
	                	modifyIngrds.add(m);
	                }
	                model.addAttribute("ingrds", modifyIngrds);
	            } catch (JsonProcessingException e) {
	                e.printStackTrace();
	            }
	        }
	        model.addAttribute("userName", userName);
	        model.addAttribute("ingrdsAlert", ingrdsAlert);
	        model.addAttribute("ingrdsAlertSize", ingrdsAlert.size());
	        return "modifyIngrds";
    	} catch (TokenExpiredException e) {
    		return "redirect:/api/v1/modifyIngrds";
		} catch (Exception e) {
			e.printStackTrace();
			return "404";
		}
    }

    @PostMapping("modifyIngrds")
    public ResponseEntity<String> modifyIngrds(@RequestBody List<FridgeIngrds> fridgeIngrds, HttpServletRequest request, Model model) throws Exception {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
                    .verify(cookieManager.getCookie("Access-Token", request));
            String id = jwt.getClaim("id").asString();

            User user = userRepository.findByUserId(id);
            for (FridgeIngrds ingrd : fridgeIngrds) {
                switch (ingrd.getFreshness()) {
                    case "F":
                        try {
                            IngrdsF ingrdsF = new IngrdsF(ingrd.getSeqId(), ingrd.getIngredientNm(), ingrd.getIngredientNum(),
                                    ingrd.getStorageMtd(), ingrd.getStorageDate(), ingrd.getExpirationDate().substring(0, 10),
                                    ingrd.getMemo(), ingrd.getFreshness(), user);
                            ingrdsFRepository.save(ingrdsF);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식재료 정보 수정에 실패했습니다.");
                        }
                        break;
                    case "S":
                        try {
                            IngrdsS ingrdsS = new IngrdsS(ingrd.getSeqId(), ingrd.getIngredientNm(), ingrd.getIngredientNum(),
                                    ingrd.getStorageMtd(), ingrd.getStorageDate(), ingrd.getExpirationDate().substring(0, 10),
                                    ingrd.getMemo(), ingrd.getFreshness(), user);
                            ingrdsSRepository.save(ingrdsS);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식재료 정보 수정에 실패했습니다.");
                        }
                        break;
                    case "E":
                        try {
                            IngrdsE ingrdsE = new IngrdsE(ingrd.getSeqId(), ingrd.getIngredientNm(), ingrd.getIngredientNum(),
                                    ingrd.getStorageMtd(), ingrd.getStorageDate(), ingrd.getExpirationDate().substring(0, 10),
                                    ingrd.getMemo(), ingrd.getFreshness(), user);
                            ingrdsERepository.save(ingrdsE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식재료 정보 수정에 실패했습니다.");
                        }
                        break;
                }
            }
            return ResponseEntity.ok("식재료 정보 수정을 완료했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("통신 문제가 발생했습니다.");
        }
    }
	
	@PostMapping("deleteIngrds")
	public ResponseEntity<String> deleteIngrds(@RequestBody List<FridgeIngrds> fridgeIngrds, HttpServletRequest request, Model model) throws Exception {
	    try {
	        for (FridgeIngrds ingrd : fridgeIngrds) {
	        	System.out.println(ingrd.toString());
	        	
	            switch (ingrd.getFreshness()) {
	                case "F":
	                    try {
	                        ingrdsFRepository.deleteBySeqId(ingrd.getSeqId());
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                        // 예외 처리 로직 작성 (실패했을 경우)
	                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식재료 정보 삭제에 실패했습니다.");
	                    }
	                    break;
	                case "S":
	                    try {
	                    	ingrdsSRepository.deleteBySeqId(ingrd.getSeqId());
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                        // 예외 처리 로직 작성 (실패했을 경우)
	                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식재료 정보 삭제에 실패했습니다.");
	                    }
	                    break;
	                case "E":
	                    try {
	                    	ingrdsERepository.deleteBySeqId(ingrd.getSeqId());
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                        // 예외 처리 로직 작성 (실패했을 경우)
	                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식재료 정보 삭제에 실패했습니다.");
	                    }
	                    break;
	            }
	        }
	        return ResponseEntity.ok("식재료 정보 삭제를 완료했습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("통신 문제가 발생했습니다.");
	    }
	}

	@PostMapping("sessionModifyIngrds")
	public ResponseEntity<String> sessionModifyIngrds(@RequestBody List<FridgeIngrds> fridgeIngrds, HttpServletRequest request,
	        Model model) throws Exception {
	    // List<FridgeIngrds>를 JSON 문자열로 변환하여 세션에 저장
	    try {
	        ObjectMapper objectMapper = new ObjectMapper();
	        request.getSession().setAttribute("data", objectMapper.writeValueAsString(fridgeIngrds));
	        return ResponseEntity.ok("식재료 정보를 담은 세션을 생성했습니다.");
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("통신 문제가 발생했습니다.");
	    }
	}
	
    @PostMapping("deleteJjim")
    public ResponseEntity <String> deleteJjim(@RequestBody Map<String, Object> input, HttpServletRequest request) throws Exception {
    	try {
    	    DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
    	                .verify(cookieManager.getCookie("Access-Token", request));
    	    String id = jwt.getClaim("id").asString();
    	        	    
    	    User user = userRepository.getById(id);
    	    List<String> jjims = new ArrayList<>(Arrays.asList(user.getUserJjim().split(","))); // 원래 찜 목록
    	    List<String> recipeIds = Arrays.asList(((String) input.get("recipeIds")).split(",")); // 삭제할 찜 목록
    	    
    	    jjims.removeAll(recipeIds);
            if (jjims.size() == 0) {
            	user.setUserJjim(null);
            } else {
            	user.setUserJjim(String.join(",", jjims));
            }
    	    userRepository.save(user);	
            return ResponseEntity.ok("찜 하기를 완료했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("통신 문제가 발생했습니다.");
        }
    }
}
