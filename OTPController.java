package com.college.admission_chatbot.controller;

import com.college.admission_chatbot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/otp")
public class OTPController {

    @Autowired
    private EmailService emailService;

    // Temporary storage for OTPs
    private Map<String, OTPData> otpStorage = new ConcurrentHashMap<>();

    @GetMapping("/debug")
    public Map<String, String> debug() {
        Map<String, String> response = new HashMap<>();
        System.out.println("\n========== DEBUG ENDPOINT CALLED ==========");
        
        try {
            // Check if EmailService is injected
            response.put("emailService_status", emailService != null ? "✅ LOADED" : "❌ NULL");
            
            if (emailService != null) {
                // Test OTP generation
                String testOtp = emailService.generateOTP();
                response.put("otp_generation", "✅ Generated: " + testOtp);
                
                // Test email send
                boolean sent = emailService.sendOTPEmail("roshanpagare45@gmail.com", testOtp);
                response.put("test_email", sent ? "✅ SENT" : "❌ FAILED");
                
                if (sent) {
                    response.put("message", "Check your email for test OTP: " + testOtp);
                }
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }

    @PostMapping("/send")
    public Map<String, String> sendOTP(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        
        System.out.println("\n========== OTP SEND REQUEST ==========");
        System.out.println("Email: " + email);
        System.out.println("EmailService: " + (emailService != null ? "✅ Present" : "❌ NULL"));
        
        try {
            // Validate email
            if (email == null || email.trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Email cannot be empty");
                return response;
            }
            
            if (!email.contains("@")) {
                response.put("status", "error");
                response.put("message", "Invalid email format");
                return response;
            }
            
            // Check if EmailService is available
            if (emailService == null) {
                throw new RuntimeException("EmailService is not initialized! Check dependency injection.");
            }
            
            // Generate OTP
            String otp = emailService.generateOTP();
            System.out.println("Generated OTP: " + otp);
            
            // Store OTP with expiry (5 minutes)
            OTPData otpData = new OTPData(otp, LocalDateTime.now().plusMinutes(5));
            otpStorage.put(email, otpData);
            System.out.println("OTP stored for: " + email);
            
            // Send OTP via email
            boolean sent = emailService.sendOTPEmail(email, otp);
            
            if (sent) {
                response.put("status", "success");
                response.put("message", "OTP sent successfully to " + email);
                System.out.println("✅ OTP process completed successfully");
            } else {
                response.put("status", "error");
                response.put("message", "Failed to send OTP. Check server logs.");
                System.err.println("❌ Email service returned false");
            }
            
        } catch (Exception e) {
            System.err.println("❌ EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/verify")
    public Map<String, String> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        Map<String, String> response = new HashMap<>();
        
        System.out.println("\n========== OTP VERIFY REQUEST ==========");
        System.out.println("Email: " + email);
        System.out.println("OTP received: " + otp);
        
        try {
            OTPData otpData = otpStorage.get(email);
            
            if (otpData == null) {
                System.out.println("❌ No OTP found for email: " + email);
                response.put("status", "error");
                response.put("message", "No OTP found. Please request again.");
                return response;
            }
            
            System.out.println("Stored OTP: " + otpData.getOtp());
            System.out.println("Expires at: " + otpData.getExpiry());
            
            if (otpData.getExpiry().isBefore(LocalDateTime.now())) {
                System.out.println("❌ OTP expired");
                response.put("status", "error");
                response.put("message", "OTP has expired. Please request again.");
                otpStorage.remove(email);
                return response;
            }
            
            if (!otpData.getOtp().equals(otp)) {
                System.out.println("❌ OTP mismatch");
                response.put("status", "error");
                response.put("message", "Invalid OTP. Please try again.");
                return response;
            }
            
            // OTP verified successfully
            response.put("status", "success");
            response.put("message", "Email verified successfully!");
            response.put("verified", "true");
            
            // Remove OTP after successful verification
            otpStorage.remove(email);
            System.out.println("✅ OTP verified successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            response.put("status", "error");
            response.put("message", "Verification failed: " + e.getMessage());
        }
        
        return response;
    }

    // Inner class to store OTP data
    private static class OTPData {
        private String otp;
        private LocalDateTime expiry;

        public OTPData(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }

        public String getOtp() { return otp; }
        public LocalDateTime getExpiry() { return expiry; }
    }
}