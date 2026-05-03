package com.college.admission_chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestEmailController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/send-otp")
    public Map<String, String> testSendOTP(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        
        try {
            System.out.println("========== TEST EMAIL START ==========");
            System.out.println("Sending to: " + email);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("roshanpagare85@gmail.com");
            message.setTo(email);
            message.setSubject("TEST EMAIL");
            message.setText("If you receive this, email is working!");
            
            mailSender.send(message);
            
            System.out.println("✅ Email sent successfully!");
            response.put("status", "success");
            response.put("message", "Email sent to " + email);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace(); // This will print full error
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }
}