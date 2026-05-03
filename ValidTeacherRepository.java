package com.college.admission_chatbot.repository;

import com.college.admission_chatbot.entity.ValidTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ValidTeacherRepository extends JpaRepository<ValidTeacher, Long> {
    
    Optional<ValidTeacher> findByTeacherId(String teacherId);
    Optional<ValidTeacher> findByEmail(String email);
    boolean existsByTeacherId(String teacherId);
    boolean existsByEmail(String email);
}