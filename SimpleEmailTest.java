package com.college.admission_chatbot.controller;

import org.springframework.web.bind.annotation.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

@RestController
@RequestMapping("/api/simple")
public class SimpleEmailTest {

    @GetMapping("/test")
    public String testEmail(@RequestParam String to) {
        try {
            System.out.println("\n========== SIMPLE EMAIL TEST ==========");
            System.out.println("Sending to: " + to);
            
            // Your Gmail credentials
            String host = "smtp.gmail.com";
            String port = "587";
            String username = "roshanpagare45@gmail.com";
            String password = "vmuk tlsh nlpd pfhq";
            
            // Setup properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.ssl.trust", host);
            
            // Create session
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            session.setDebug(true);
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Test Email from College Portal");
            message.setText("If you receive this, email configuration is WORKING!");
            
            // Send
            Transport.send(message);
            
            return "✅ SUCCESS: Email sent to " + to;
            
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ ERROR: " + e.getMessage();
        }
    }
}