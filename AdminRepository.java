package com.college.admission_chatbot.repository;

import com.college.admission_chatbot.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    // Basic authentication
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByEmailAndPassword(String email, String password);
    Optional<Admin> findByEmailAndSecurityQuestionAndSecurityAnswer(
        String email, String securityQuestion, String securityAnswer);
    boolean existsByEmail(String email);
    
    // Teacher verification
    boolean existsByTeacherId(String teacherId);
    Optional<Admin> findByTeacherId(String teacherId);
    Optional<Admin> findByVerificationToken(String token);
    List<Admin> findByIsVerifiedTrue();
    List<Admin> findByIsVerifiedFalse();
    
    // Email verification
    Optional<Admin> findByPersonalEmail(String personalEmail);
    boolean existsByPersonalEmail(String personalEmail);
    
    @Query("SELECT CASE WHEN a.email LIKE %:domain% THEN true ELSE false END FROM Admin a WHERE a.email = :email")
    boolean isCollegeEmail(@Param("email") String email, @Param("domain") String domain);
}