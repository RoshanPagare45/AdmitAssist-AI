package com.college.admission_chatbot.repository;

import com.college.admission_chatbot.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByCourseName(String courseName);

    List<Course> findBySpecialization(String specialization);
}