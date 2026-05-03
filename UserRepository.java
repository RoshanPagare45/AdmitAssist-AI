package com.college.admission_chatbot.repository;

import com.college.admission_chatbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByMobile(String mobile);
    
    List<User> findBySessionId(String sessionId);
    
    Optional<User> findByEmailAndPassword(String email, String password);
    
    @Query(value = "SELECT * FROM users WHERE full_name LIKE CONCAT('%', :search, '%') OR email LIKE CONCAT('%', :search, '%') OR mobile LIKE CONCAT('%', :search, '%') ORDER BY registered_at DESC", nativeQuery = true)
    List<User> searchUsers(@Param("search") String search);
    
    @Query(value = "SELECT * FROM users ORDER BY registered_at DESC", nativeQuery = true)
    List<User> findAllByOrderByRegisteredAtDesc();
}