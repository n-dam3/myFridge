package com.cos.myFridge.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import com.cos.myFridge.dto.Mail;
import com.cos.myFridge.model.User;
import com.cos.myFridge.model.IngrdsE;
import com.cos.myFridge.model.IngrdsF;
import com.cos.myFridge.model.IngrdsS;
import com.cos.myFridge.repository.IngrdsERepository;
import com.cos.myFridge.repository.IngrdsFRepository;
import com.cos.myFridge.repository.IngrdsSRepository;
import com.cos.myFridge.repository.UserRepository;
import com.cos.myFridge.service.CookieManager;
import com.cos.myFridge.service.MailService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Controller
@AllArgsConstructor
@RequiredArgsConstructor
@EnableScheduling
public class MailController {
	@Autowired
	private MailService mailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private IngrdsERepository ingrdsERepository;
	
	@Autowired
	private IngrdsFRepository ingrdsFRepository;
	
	@Autowired
	private IngrdsSRepository ingrdsSRepository;
	
	@Autowired
	private CookieManager cookieManager;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
		
	@PostMapping("/sendMailControl")
	public String findPassword(User inputId, HttpServletResponse response) throws Exception {
		String userId = inputId.getUserId();
		String userName = userRepository.findByUserId(userId).getUserName();
		String authNum = UUID.randomUUID().toString().substring(0, 8); // 랜덤 인증번호
		
        cookieManager.setCookie("Auth-Info", Base64.getEncoder().encodeToString((userId + "#" + bCryptPasswordEncoder.encode(authNum)).getBytes()), response, 300);
		
		Mail mail = new Mail();
		mail.setAddress(userId);
		mail.setTitle("【myFridge】" + authNum + "는 귀하의 인증코드입니다.");
		mail.setMessage(userName + "님께\n\n귀하의 인증번호  : " + authNum
				+ "\n5분 이내에 인증번호를 입력하시기 바랍니다.\n귀하가 신청한 게 아니거나 다른 문제가 있다면 메일(###@###.###)로 문의해 주세요.\n\nmyFridge에 보내주신 성원에 감사드립니다!");
        
		mailService.send(mail);
        
		return "redirect:/checkAN";
	}

    @Scheduled(cron = "0 0 4 * * ?") // 새벽 4시에 업데이트
    public void update() {
        LocalDate today = LocalDate.now();
        
    	List<IngrdsF> ingrdsF = ingrdsFRepository.findAll();        
        for (IngrdsF ingrdF : ingrdsF) {
            int storageDate = Integer.parseInt(ingrdF.getStorageDate());    
            LocalDate expirationDate = LocalDate.parse(ingrdF.getExpirationDate().substring(0, 10));
            long daysLeft = Period.between(today, expirationDate).getDays();
            if (daysLeft < 0) {
            	IngrdsE ingrdsE = new IngrdsE(ingrdF.getSeqId(), ingrdF.getIngredientNm(), ingrdF.getIngredientNum(), ingrdF.getStorageMtd(), ingrdF.getStorageDate(),
            			ingrdF.getExpirationDate(), ingrdF.getMemo(), "E", ingrdF.getUser());
            	ingrdsFRepository.delete(ingrdF);
            	ingrdsERepository.save(ingrdsE);
            } else if (daysLeft < 5) {
            	IngrdsS ingrdsS = new IngrdsS(ingrdF.getSeqId(), ingrdF.getIngredientNm(), ingrdF.getIngredientNum(), ingrdF.getStorageMtd(), ingrdF.getStorageDate(),
            			ingrdF.getExpirationDate(), ingrdF.getMemo(), "S", ingrdF.getUser());
            	ingrdsFRepository.delete(ingrdF);
            	ingrdsSRepository.save(ingrdsS);            	
            } else {
	            storageDate++;
	            ingrdF.setStorageDate(String.valueOf(storageDate));
	            ingrdsFRepository.save(ingrdF);
            }
        }

    	List<IngrdsS> ingrdsS = ingrdsSRepository.findAll();
        for (IngrdsS ingrdS : ingrdsS) {
            int storageDate = Integer.parseInt(ingrdS.getStorageDate());    
            LocalDate expirationDate = LocalDate.parse(ingrdS.getExpirationDate().substring(0, 10));
            long daysLeft = Period.between(today, expirationDate).getDays();
            if (daysLeft < 0) {
            	IngrdsE ingrdsE = new IngrdsE(ingrdS.getSeqId(), ingrdS.getIngredientNm(), ingrdS.getIngredientNum(), ingrdS.getStorageMtd(), ingrdS.getStorageDate(),
            			ingrdS.getExpirationDate(), ingrdS.getMemo(), "E", ingrdS.getUser());
            	ingrdsSRepository.delete(ingrdS);
            	ingrdsERepository.save(ingrdsE);
            } else {
	            storageDate++;
	            ingrdS.setStorageDate(String.valueOf(storageDate));
	            ingrdsSRepository.save(ingrdS);
            }
        }

        List<IngrdsE> ingrdsE = ingrdsERepository.findAll();
        for (IngrdsE ingrdE : ingrdsE) {
            int storageDate = Integer.parseInt(ingrdE.getStorageDate());
            storageDate++;
            ingrdE.setStorageDate(String.valueOf(storageDate));
            ingrdsERepository.save(ingrdE);
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 18 * * *") // 저녁 6시에 메일링
    public void sendEmail() {
    	LocalDate today = LocalDate.now();
    	List<IngrdsS> ingrdsS = ingrdsSRepository.findAll();
        List<IngrdsE> ingrdsE = ingrdsERepository.findAll();
        Map<String, Mail> mails = new HashMap<>();
        
        for (IngrdsS ingrdS : ingrdsS) { 
            LocalDate expirationDate = LocalDate.parse(ingrdS.getExpirationDate().substring(0, 10));
            long daysLeft = Period.between(today, expirationDate).getDays();
            String ingredientNm = ingrdS.getIngredientNm();
            String msgText = "";
            
	        if (daysLeft < 0) {     	        
	        	msgText += (ingredientNm + "은(는) 소비기한이 " + Math.abs(daysLeft) + "일 지났습니다.(" + expirationDate + ")\n");
	        } else {
	        	msgText += (ingredientNm + "은(는) 소비기한이 " + daysLeft + "일 남았습니다.(" + expirationDate + ")\n");
	        }
            
        	if (mails.containsKey(ingrdS.getUser().getUserId())) {
        		Mail mail = mails.get(ingrdS.getUser().getUserId());
        		msgText += mail.getMessage();
        		mail.setMessage(msgText);
        	} else {
        		Mail mail = new Mail();
        		mail.setAddress(ingrdS.getUser().getUserId());
        		mail.setTitle("【myFridge】보관기한 임박 식재료 알림입니다.");
        		mail.setMessage(msgText);
        		mails.put(ingrdS.getUser().getUserId(), mail);
        	}
        }
        
        for (IngrdsE ingrdE : ingrdsE) {
            LocalDate expirationDate = LocalDate.parse(ingrdE.getExpirationDate().substring(0, 10));
            long daysLeft = Period.between(today, expirationDate).getDays();
            String ingredientNm = ingrdE.getIngredientNm();
            String msgText = "";
            
	        if (daysLeft < 0) {     	        
	        	msgText += (ingredientNm + "은(는) 소비기한이 " + Math.abs(daysLeft) + "일 지났습니다.(" + expirationDate + ")\n");
	        } else {
	        	msgText += (ingredientNm + "은(는) 소비기한이 " + daysLeft + "일 남았습니다.(" + expirationDate + ")\n");
	        }
            
        	if (mails.containsKey(ingrdE.getUser().getUserId())) {
        		Mail mail = mails.get(ingrdE.getUser().getUserId());
        		msgText += mail.getMessage();
        		mail.setMessage(msgText);
        	} else {
        		Mail mail = new Mail();
        		mail.setAddress(ingrdE.getUser().getUserId());
        		mail.setTitle("【myFridge】보관기한 임박 식재료 알림입니다.");
        		mail.setMessage(msgText);
        		mails.put(ingrdE.getUser().getUserId(), mail);
        	}
        }
         
        for (Map.Entry<String, Mail> entry : mails.entrySet()) {
            Mail mail = entry.getValue();
            System.out.println(mail.toString());
            mailService.send(mail);
        }
    }
}
