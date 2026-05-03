package com.college.admission_chatbot.controller;

import com.college.admission_chatbot.entity.User;
import com.college.admission_chatbot.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class StudentAuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/student/signin")
    public String showLogin() {
        return "student-login";
    }

    @GetMapping("/student/signup")
    public String showSignup() {
        return "student-register";
    }

    @PostMapping("/student/signup")
    public String signup(@RequestParam String fullName,
                         @RequestParam String email,
                         @RequestParam String mobile,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         RedirectAttributes redirectAttributes) {
        
        if (fullName == null || fullName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Full name is required");
            return "redirect:/student/signup";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            redirectAttributes.addFlashAttribute("error", "Valid email is required");
            return "redirect:/student/signup";
        }
        if (!mobile.matches("\\d{10}")) {
            redirectAttributes.addFlashAttribute("error", "Valid 10-digit mobile number is required");
            return "redirect:/student/signup";
        }
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters");
            return "redirect:/student/signup";
        }
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/student/signup";
        }
        
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already registered");
            return "redirect:/student/signin";
        }
        
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPassword(password);
        user.setRegisteredAt(LocalDateTime.now());
        user.setLoginCount(0);
        user.setIsActive(true);
        userRepository.save(user);
        
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please sign in.");
        return "redirect:/student/signin";
    }

    @PostMapping("/student/signin")
    public String signin(@RequestParam String email,
                         @RequestParam String password,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email not found");
            return "redirect:/student/signup";
        }
        
        User user = userOptional.get();
        
        if (!user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid password");
            return "redirect:/student/signin";
        }
        
        session.setAttribute("studentId", user.getId());
        session.setAttribute("studentName", user.getFullName());
        
        user.setLastLogin(LocalDateTime.now());
        user.setLoginCount(user.getLoginCount() + 1);
        userRepository.save(user);
        
        // Redirect to index page (which is now in templates folder)
        return "redirect:/index";
    }
    
    @GetMapping("/student/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}