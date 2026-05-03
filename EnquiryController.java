package com.college.admission_chatbot.controller;

import com.college.admission_chatbot.entity.Enquiry;
import com.college.admission_chatbot.entity.User;
import com.college.admission_chatbot.repository.EnquiryRepository;
import com.college.admission_chatbot.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/enquiries")
public class EnquiryController {

    @Autowired
    private EnquiryRepository enquiryRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public Enquiry saveEnquiry(@Valid @RequestBody Enquiry enquiry, HttpSession session) {
        String sessionId = session.getId();
        List<User> existingUserList = userRepository.findBySessionId(sessionId);
        
        if (!existingUserList.isEmpty()) {
            User existingUser = existingUserList.get(0);
            enquiry.setUser(existingUser);
        } else {
            Optional<User> userByEmail = userRepository.findByEmail(enquiry.getEmail());
            if (userByEmail.isPresent()) {
                enquiry.setUser(userByEmail.get());
                userByEmail.get().setSessionId(sessionId);
                userRepository.save(userByEmail.get());
            } else {
                // Create new user - now with 4 parameters including password
                User newUser = new User();
                newUser.setFullName(enquiry.getFullName());
                newUser.setEmail(enquiry.getEmail());
                newUser.setMobile(enquiry.getMobile());
                newUser.setPassword("default123"); // Set a default password
                newUser.setSessionId(sessionId);
                User savedUser = userRepository.save(newUser);
                enquiry.setUser(savedUser);
            }
        }
        
        return enquiryRepository.save(enquiry);
    }

    @GetMapping
    public List<Enquiry> getAllEnquiries() {
        return enquiryRepository.findAll();
    }
    
    @GetMapping("/user/{userId}")
    public List<Enquiry> getEnquiriesByUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(enquiryRepository::findByUserOrderByCreatedAtDesc).orElse(List.of());
    }
    
    @GetMapping("/most-asked")
    public List<Object[]> getMostAskedQueries() {
        return enquiryRepository.findTop10MostAskedQueries();
    }
    
    @GetMapping("/by-course")
    public List<Object[]> getEnquiriesByCourse() {
        return enquiryRepository.countEnquiriesByCourse();
    }
    
    @GetMapping("/by-user")
    public List<Object[]> getEnquiriesByUser() {
        return enquiryRepository.countEnquiriesByUser();
    }

    @DeleteMapping("/{id}")
    public String deleteEnquiry(@PathVariable Long id) {
        enquiryRepository.deleteById(id);
        return "Deleted Successfully";
    }
}