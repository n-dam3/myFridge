package com.cos.myFridge.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cos.myFridge.dto.Mail;

import org.springframework.mail.SimpleMailMessage;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailService {	
    private JavaMailSender mailSender;

    @Async
    public void send(Mail mail) {
        // 단순 텍스트 구성 메일 메시지 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getAddress());
        message.setSubject(mail.getTitle());
        message.setText(mail.getMessage());

        mailSender.send(message);   // 메일 발송
    }
}