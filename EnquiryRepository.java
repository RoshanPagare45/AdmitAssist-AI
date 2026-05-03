package com.college.admission_chatbot.repository;

import com.college.admission_chatbot.entity.Enquiry;
import com.college.admission_chatbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
    
    List<Enquiry> findByUserOrderByCreatedAtDesc(User user);
    
    @Query(value = "SELECT message, COUNT(*) as count FROM enquiry WHERE message IS NOT NULL AND message != '' GROUP BY message ORDER BY count DESC LIMIT 10", nativeQuery = true)
    List<Object[]> findTop10MostAskedQueries();
    
    @Query("SELECT e.course, COUNT(e) FROM Enquiry e GROUP BY e.course")
    List<Object[]> countEnquiriesByCourse();
    
    @Query("SELECT u.fullName, COUNT(e) FROM Enquiry e JOIN e.user u GROUP BY u.id ORDER BY COUNT(e) DESC")
    List<Object[]> countEnquiriesByUser();
}