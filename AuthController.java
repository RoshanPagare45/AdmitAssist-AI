package com.college.admission_chatbot.controller;

import com.college.admission_chatbot.entity.Admin;
import com.college.admission_chatbot.entity.ValidTeacher;
import com.college.admission_chatbot.repository.AdminRepository;
import com.college.admission_chatbot.repository.ValidTeacherRepository;
import com.college.admission_chatbot.service.AdminVerificationService;
import com.college.admission_chatbot.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ValidTeacherRepository validTeacherRepository;

    @Autowired
    private AdminVerificationService verificationService;
    
    @Autowired
    private EmailService emailService;

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("securityQuestions", getSecurityQuestions());
        model.addAttribute("departments", getDepartments());
        return "admin-signup";
    }

    @PostMapping("/signup")
    public String processSignup(
            @RequestParam String fullName,
            @RequestParam String personalEmail,
            @RequestParam String collegeEmail,
            @RequestParam String mobile,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String securityQuestion,
            @RequestParam String securityAnswer,
            @RequestParam String teacherId,
            @RequestParam String department,
            @RequestParam String designation,
            @RequestParam(required = false, defaultValue = "false") boolean emailVerified,
            RedirectAttributes redirectAttributes) {

        // Basic validation
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
            return "redirect:/auth/signup";
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters!");
            return "redirect:/auth/signup";
        }

        if (!mobile.matches("[0-9]{10}")) {
            redirectAttributes.addFlashAttribute("error", "Mobile number must be 10 digits!");
            return "redirect:/auth/signup";
        }

        // Check if email is verified via OTP
        if (!emailVerified) {
            redirectAttributes.addFlashAttribute("error", "Please verify your email first!");
            return "redirect:/auth/signup";
        }

        // Check if already registered
        if (adminRepository.existsByEmail(collegeEmail)) {
            redirectAttributes.addFlashAttribute("error", "College email already registered!");
            return "redirect:/auth/signup";
        }

        if (adminRepository.existsByTeacherId(teacherId)) {
            redirectAttributes.addFlashAttribute("error", "Teacher ID already registered!");
            return "redirect:/auth/signup";
        }

        // Verify against valid_teachers table
        ValidTeacher validTeacher = validTeacherRepository.findByTeacherId(teacherId).orElse(null);
        
        if (validTeacher == null) {
            redirectAttributes.addFlashAttribute("error", 
                "❌ Invalid Teacher ID! This ID is not registered in college database.");
            return "redirect:/auth/signup";
        }

        // Check college email match
        if (!validTeacher.getEmail().equalsIgnoreCase(collegeEmail)) {
            redirectAttributes.addFlashAttribute("error", 
                "❌ College email does not match our records for this Teacher ID.");
            return "redirect:/auth/signup";
        }

        // Create new admin
        Admin admin = new Admin();
        admin.setFullName(validTeacher.getFullName());
        admin.setPersonalEmail(personalEmail);
        admin.setCollegeEmail(collegeEmail);
        admin.setEmail(collegeEmail);
        admin.setMobile(mobile);
        admin.setPassword(password);
        admin.setSecurityQuestion(securityQuestion);
        admin.setSecurityAnswer(securityAnswer.toLowerCase().trim());
        
        // Set teacher details
        admin.setTeacherId(teacherId);
        admin.setDepartment(validTeacher.getDepartment());
        admin.setDesignation(validTeacher.getDesignation());
        
        // Mark as verified (since OTP was verified)
        admin.setEmailVerified(true);
        
        // Auto-verify teacher account
        if (verificationService.autoVerifyTeacher(teacherId)) {
            admin.setVerified(true);
            admin.setVerifiedAt(LocalDateTime.now());
        } else {
            admin.setVerified(false);
            admin.setVerificationToken(verificationService.generateVerificationToken());
        }
        
        admin.setCreatedAt(LocalDateTime.now());

        adminRepository.save(admin);

        // Send success email to personal email
        try {
            emailService.sendRegistrationSuccessEmail(personalEmail, validTeacher.getFullName());
        } catch (Exception e) {
            System.err.println("Failed to send success email: " + e.getMessage());
        }

        if (admin.isVerified()) {
            redirectAttributes.addFlashAttribute("success", 
                "✓ Registration successful! Your account is verified. You can login now.");
        } else {
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! Your account is pending verification.");
        }
        
        return "redirect:/auth/signin";
    }

    @GetMapping("/signin")
    public String showSigninPage() {
        return "admin-signin";
    }

    @PostMapping("/signin")
    public String processSignin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        Admin admin = adminRepository.findByEmailAndPassword(email, password).orElse(null);

        if (admin == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password!");
            return "redirect:/auth/signin";
        }

        // Check if email was verified via OTP
        if (!admin.isEmailVerified()) {
            redirectAttributes.addFlashAttribute("error", 
                "Your email was not verified. Please contact administration.");
            return "redirect:/auth/signin";
        }

        if (!admin.isVerified()) {
            redirectAttributes.addFlashAttribute("error", 
                "Your account is pending verification. Please wait for HR approval.");
            return "redirect:/auth/signin";
        }

        if (!admin.isActive()) {
            redirectAttributes.addFlashAttribute("error", "Account is deactivated. Contact HR department!");
            return "redirect:/auth/signin";
        }

        // Update login tracking
        admin.setLastLogin(LocalDateTime.now());
        admin.setLastActivity(LocalDateTime.now());
        admin.setSessionId(session.getId());
        adminRepository.save(admin);

        // Set session attributes
        session.setAttribute("adminId", admin.getId());
        session.setAttribute("adminName", admin.getFullName());
        session.setAttribute("adminEmail", admin.getEmail());
        session.setAttribute("adminDepartment", admin.getDepartment());
        session.setAttribute("loginTime", LocalDateTime.now().toString());
        
        // 24 HOURS SESSION (24 * 60 * 60 = 86400 seconds)
        session.setMaxInactiveInterval(24 * 60 * 60);

        // Log for debugging
        System.out.println("✅ User logged in: " + admin.getEmail());
        System.out.println("⏰ Session timeout set to: 24 hours");
        System.out.println("🆔 Session ID: " + session.getId());

        return "redirect:/dashboard";
    }

    @GetMapping("/admin/pending-verifications")
    public String showPendingVerifications(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminId") == null) {
            redirectAttributes.addFlashAttribute("error", "Please login first!");
            return "redirect:/auth/signin";
        }
        
        List<Admin> pendingTeachers = adminRepository.findByIsVerifiedFalse();
        List<Admin> allTeachers = adminRepository.findAll();
        long verifiedCount = adminRepository.findByIsVerifiedTrue().size();
        
        model.addAttribute("pendingTeachers", pendingTeachers);
        model.addAttribute("totalPending", pendingTeachers.size());
        model.addAttribute("totalVerified", verifiedCount);
        model.addAttribute("totalAll", allTeachers.size());
        
        return "admin-pending-verifications";
    }

    @GetMapping("/verify-teacher/{id}")
    public String verifyTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            redirectAttributes.addFlashAttribute("error", "Please login first!");
            return "redirect:/auth/signin";
        }
        
        // Update last activity
        updateLastActivity(session);
        
        Admin verified = verificationService.verifyTeacher(id);
        if (verified != null) {
            redirectAttributes.addFlashAttribute("success", "Teacher verified successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Teacher not found!");
        }
        return "redirect:/auth/admin/pending-verifications";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("securityQuestions", getSecurityQuestions());
        model.addAttribute("step", 1);
        return "admin-forgot-password";
    }

    @PostMapping("/verify-identity")
    public String verifyIdentity(
            @RequestParam String email,
            @RequestParam String securityQuestion,
            @RequestParam String securityAnswer,
            Model model,
            RedirectAttributes redirectAttributes) {

        Admin admin = adminRepository.findByEmail(email).orElse(null);

        if (admin == null) {
            redirectAttributes.addFlashAttribute("error", "Email not found!");
            return "redirect:/auth/forgot-password";
        }

        if (!admin.isVerified()) {
            redirectAttributes.addFlashAttribute("error", "Account not verified yet!");
            return "redirect:/auth/forgot-password";
        }

        if (!admin.getSecurityQuestion().equals(securityQuestion) ||
                !admin.getSecurityAnswer().equals(securityAnswer.toLowerCase().trim())) {
            redirectAttributes.addFlashAttribute("error", "Security answer is incorrect!");
            return "redirect:/auth/forgot-password";
        }

        model.addAttribute("email", email);
        model.addAttribute("step", 2);
        model.addAttribute("securityQuestions", getSecurityQuestions());
        return "admin-forgot-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
            return "redirect:/auth/forgot-password?step=2&email=" + email;
        }

        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters!");
            return "redirect:/auth/forgot-password?step=2&email=" + email;
        }

        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/auth/forgot-password";
        }

        admin.setPassword(newPassword);
        adminRepository.save(admin);

        redirectAttributes.addFlashAttribute("success", "Password reset successful! Please login with new password.");
        return "redirect:/auth/signin";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String sessionId = session.getId();
        session.invalidate();
        System.out.println("✅ User logged out. Session invalidated: " + sessionId);
        return "redirect:/";
    }

    @GetMapping("/session-info")
    @ResponseBody
    public Map<String, Object> getSessionInfo(HttpSession session) {
        Map<String, Object> info = new HashMap<>();
        info.put("sessionId", session.getId());
        info.put("maxInactiveInterval", session.getMaxInactiveInterval());
        info.put("maxInactiveIntervalHours", session.getMaxInactiveInterval() / 3600);
        info.put("creationTime", session.getCreationTime());
        info.put("lastAccessedTime", session.getLastAccessedTime());
        info.put("isLoggedIn", session.getAttribute("adminId") != null);
        info.put("adminName", session.getAttribute("adminName"));
        return info;
    }

    @GetMapping("/keep-alive")
    @ResponseBody
    public String keepAlive(HttpSession session) {
        // Simple endpoint to keep session alive
        updateLastActivity(session);
        return "Session active";
    }

    // Helper method to update last activity
    private void updateLastActivity(HttpSession session) {
        Long adminId = (Long) session.getAttribute("adminId");
        if (adminId != null) {
            Admin admin = adminRepository.findById(adminId).orElse(null);
            if (admin != null) {
                admin.setLastActivity(LocalDateTime.now());
                adminRepository.save(admin);
            }
        }
    }

    private Map<String, String> getSecurityQuestions() {
        Map<String, String> questions = new HashMap<>();
        questions.put("pet", "What was your first pet's name?");
        questions.put("school", "What was the name of your first school?");
        questions.put("mother", "What is your mother's maiden name?");
        questions.put("city", "In which city were you born?");
        questions.put("book", "What is your favorite book?");
        questions.put("movie", "What is your favorite movie?");
        return questions;
    }

    private Map<String, String> getDepartments() {
        Map<String, String> depts = new HashMap<>();
        depts.put("MCA", "MCA Department");
        depts.put("ME", "ME Department");
        depts.put("Computer", "Computer Engineering");
        depts.put("IT", "Information Technology");
        depts.put("Civil", "Civil Engineering");
        depts.put("Electrical", "Electrical Engineering");
        depts.put("Mechanical", "Mechanical Engineering");
        return depts;
    }
}