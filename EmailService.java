package com.college.admission_chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    // Generate 6-digit OTP
    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Send OTP to email
    public boolean sendOTPEmail(String toEmail, String otp) {
        try {
            System.out.println("========== SENDING OTP EMAIL ==========");
            System.out.println("From: " + fromEmail);
            System.out.println("To: " + toEmail);
            System.out.println("OTP: " + otp);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("R.H. Sapat College - Email Verification OTP");
            message.setText(String.format(
                "Dear Teacher,\n\n" +
                "Your OTP for email verification is: %s\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Regards,\n" +
                "R.H. Sapat College Administration", otp));
            
            mailSender.send(message);
            System.out.println("✅ OTP email sent successfully");
            return true;
            
        } catch (MailException e) {
            System.err.println("❌ Mail error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Send registration success email
    public boolean sendRegistrationSuccessEmail(String toEmail, String name) {
        try {
            System.out.println("========== SENDING REGISTRATION SUCCESS EMAIL ==========");
            System.out.println("To: " + toEmail);
            System.out.println("Name: " + name);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("R.H. Sapat College - Registration Successful");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Your teacher registration has been successfully completed!\n\n" +
                "You can now login to the Teacher Portal using your credentials.\n\n" +
                "Login URL: http://localhost:7075/auth/signin\n\n" +
                "If you have any issues, please contact the administration.\n\n" +
                "Regards,\n" +
                "R.H. Sapat College Administration", name));
            
            mailSender.send(message);
            System.out.println("✅ Registration success email sent");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send success email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}