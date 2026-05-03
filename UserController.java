package com.college.admission_chatbot.controller;

import com.college.admission_chatbot.entity.ChatLog;
import com.college.admission_chatbot.entity.User;
import com.college.admission_chatbot.repository.ChatLogRepository;
import com.college.admission_chatbot.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChatLogRepository chatLogRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user, HttpSession session) {
        try {
            System.out.println("=== REGISTERING USER ===");
            System.out.println("Name: " + user.getFullName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Mobile: " + user.getMobile());
            
            // Validate input
            if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name is required");
            }
            if (user.getEmail() == null || !user.getEmail().contains("@")) {
                return ResponseEntity.badRequest().body("Valid email is required");
            }
            if (user.getMobile() == null || user.getMobile().length() != 10) {
                return ResponseEntity.badRequest().body("Valid 10-digit mobile is required");
            }
            
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser.isEmpty()) {
                existingUser = userRepository.findByMobile(user.getMobile());
            }
            
            User savedUser;
            if (existingUser.isPresent()) {
                savedUser = existingUser.get();
                System.out.println("Existing user logged in: " + savedUser.getEmail());
            } else {
                user.setRegisteredAt(java.time.LocalDateTime.now());
                // Don't set session_id - it's now nullable
                savedUser = userRepository.save(user);
                System.out.println("NEW user registered: " + savedUser.getEmail() + " with ID: " + savedUser.getId());
            }
            
            // Store user ID in session for chat linking
            session.setAttribute("userId", savedUser.getId());
            System.out.println("Session userId set to: " + savedUser.getId());
            
            return ResponseEntity.ok(savedUser);
            
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }
    
   @GetMapping("/check-session")
    public User checkSession(HttpSession session) {
    Long studentId = (Long) session.getAttribute("studentId");
    System.out.println("Check session - studentId: " + studentId);
    if (studentId != null) {
        return userRepository.findById(studentId).orElse(null);
    }
    return null;
}
    
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String search) {
        System.out.println("=== SEARCH API CALLED ===");
        System.out.println("Search term: '" + search + "'");
        List<User> results = userRepository.searchUsers(search);
        System.out.println("Found " + results.size() + " users");
        return results;
    }
    
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @GetMapping("/{userId}/chats")
    public List<ChatLog> getUserChats(@PathVariable Long userId) {
        System.out.println("=== GET CHATS FOR USER ID: " + userId);
        List<ChatLog> chats = chatLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        System.out.println("Found " + chats.size() + " chats");
        return chats;
    }

    @GetMapping("/current")
    public User getCurrentUser(HttpSession session) {
    Long studentId = (Long) session.getAttribute("studentId");
    if (studentId != null) {
        return userRepository.findById(studentId).orElse(null);
    }
    return null;
}
}