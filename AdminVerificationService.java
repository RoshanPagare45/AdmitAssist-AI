package com.college.admission_chatbot.service;

import com.college.admission_chatbot.entity.Admin;
import com.college.admission_chatbot.entity.ValidTeacher;
import com.college.admission_chatbot.repository.AdminRepository;
import com.college.admission_chatbot.repository.ValidTeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminVerificationService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ValidTeacherRepository validTeacherRepository;

    private static final String COLLEGE_DOMAIN = "@rhsapat.edu.in";
    
    private static final List<String> AUTO_VERIFY_IDS = List.of(
        "TCH001", "TCH002", "TCH003", "TCH004", "TCH005",
        "FAC001", "FAC002", "FAC003", "PROF001", "PROF002"
    );

    public ValidationResult validateTeacherRegistration(String email, String teacherId, String department, 
                                                        String fullName, String designation, String mobile) {
        
        if (!email.endsWith(COLLEGE_DOMAIN)) {
            return new ValidationResult(false, "Only college email addresses (@rhsapat.edu.in) are allowed!");
        }
        
        ValidTeacher validTeacher = validTeacherRepository.findByTeacherId(teacherId).orElse(null);
        
        if (validTeacher == null) {
            return new ValidationResult(false, 
                "❌ Invalid Teacher ID! This ID is not registered in college database.");
        }
        
        if (!validTeacher.getEmail().equalsIgnoreCase(email)) {
            return new ValidationResult(false, 
                "❌ Email does not match our records for this Teacher ID.");
        }
        
        if (!validTeacher.getFullName().toLowerCase().contains(fullName.toLowerCase().split(" ")[0])) {
            return new ValidationResult(false, 
                "❌ Name does not match our records.");
        }
        
        if (adminRepository.existsByTeacherId(teacherId)) {
            return new ValidationResult(false, "This Teacher ID is already registered!");
        }
        
        if (adminRepository.existsByEmail(email)) {
            return new ValidationResult(false, "This email is already registered!");
        }
        
        boolean exactMatch = validTeacher.getFullName().equalsIgnoreCase(fullName) &&
                            validTeacher.getDepartment().equalsIgnoreCase(department) &&
                            validTeacher.getDesignation().equalsIgnoreCase(designation);
        
        return new ValidationResult(true, "Validation successful", exactMatch, validTeacher);
    }

    public String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    public Admin verifyTeacher(Long adminId) {
        Admin admin = adminRepository.findById(adminId).orElse(null);
        if (admin != null && !admin.isVerified()) {
            admin.setVerified(true);
            admin.setVerifiedAt(LocalDateTime.now());
            admin.setVerificationToken(null);
            return adminRepository.save(admin);
        }
        return null;
    }

    public boolean autoVerifyTeacher(String teacherId) {
        return AUTO_VERIFY_IDS.contains(teacherId);
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final boolean autoVerify;
        private final ValidTeacher validTeacher;

        public ValidationResult(boolean valid, String message) {
            this(valid, message, false, null);
        }

        public ValidationResult(boolean valid, String message, boolean autoVerify, ValidTeacher validTeacher) {
            this.valid = valid;
            this.message = message;
            this.autoVerify = autoVerify;
            this.validTeacher = validTeacher;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public boolean isAutoVerify() { return autoVerify; }
        public ValidTeacher getValidTeacher() { return validTeacher; }
    }
}