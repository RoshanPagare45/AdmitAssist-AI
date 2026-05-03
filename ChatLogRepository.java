package com.college.admission_chatbot.repository;

import com.college.admission_chatbot.entity.ChatLog;
import com.college.admission_chatbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
    
    List<ChatLog> findByUserOrderByCreatedAtDesc(User user);
    
    @Query(value = "SELECT * FROM chat_logs WHERE user_id = ?1 ORDER BY created_at DESC", nativeQuery = true)
    List<ChatLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Normalize: lowercase, trim spaces, and standardize common variations
    @Query(value = "SELECT CASE " +
           "WHEN LOWER(TRIM(user_message)) LIKE '%mca%fee%' THEN 'mca fees' " +
           "WHEN LOWER(TRIM(user_message)) LIKE '%me%fee%' THEN 'me fees' " +
           "WHEN LOWER(TRIM(user_message)) LIKE '%placement%' THEN 'placement' " +
           "WHEN LOWER(TRIM(user_message)) LIKE '%admission%date%' THEN 'admission dates' " +
           "WHEN LOWER(TRIM(user_message)) LIKE '%fee%structure%' THEN 'fee structure' " +
           "WHEN LOWER(TRIM(user_message)) LIKE '%scholarship%' THEN 'scholarship' " +
           "WHEN LOWER(TRIM(user_message)) LIKE '%hostel%' THEN 'hostel' " +
           "WHEN LOWER(TRIM(user_message)) LIKE '%course%recommendation%' THEN 'course recommendation' " +
           "WHEN LOWER(TRIM(user_message)) LIKE '%cut%off%' THEN 'cut-off tracker' " +
           "ELSE LOWER(TRIM(user_message)) END as question, COUNT(*) as count " +
           "FROM chat_logs WHERE user_message IS NOT NULL AND user_message != '' " +
           "GROUP BY question ORDER BY count DESC LIMIT 10", nativeQuery = true)
    List<Object[]> findTop10MostAskedQuestions();
    
    long countByUser(User user);
}