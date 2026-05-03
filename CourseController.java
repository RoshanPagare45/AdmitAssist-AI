package com.college.admission_chatbot.controller;

import com.college.admission_chatbot.entity.Course;
import com.college.admission_chatbot.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    // GET all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // ADD new course
    @PostMapping
    public Course addCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    // DELETE course
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "Course deleted successfully!";
    }

    // SEARCH by course name
    @GetMapping("/name/{courseName}")
    public List<Course> getByCourseName(@PathVariable String courseName) {
        return courseRepository.findByCourseName(courseName);
    }

    // SEARCH by specialization
    @GetMapping("/specialization/{specialization}")
    public List<Course> getBySpecialization(@PathVariable String specialization) {
        return courseRepository.findBySpecialization(specialization);
    }
}