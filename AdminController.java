package com.college.admission_chatbot.controller;

import com.college.admission_chatbot.entity.User;
import com.college.admission_chatbot.repository.ChatLogRepository;
import com.college.admission_chatbot.repository.EnquiryRepository;
import com.college.admission_chatbot.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private ChatLogRepository chatLogRepository;
    
    @Autowired
    private EnquiryRepository enquiryRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin")
    public String adminLogin() {
        return "redirect:/auth/signin";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminId") == null) {
            redirectAttributes.addFlashAttribute("error", "Please login first!");
            return "redirect:/auth/signin";
        }
        return "admin-dashboard";
    }
    
    @GetMapping("/admin/most-asked")
    @ResponseBody
    public List<Object[]> getMostAskedQuestions() {
        return chatLogRepository.findTop10MostAskedQuestions();
    }
    
    @GetMapping("/admin/all-users")
    @ResponseBody
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}