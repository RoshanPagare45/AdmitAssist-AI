package com.college.admission_chatbot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;
    private String specialization;
    private String duration;
    private int intake;
    private double fees;
    private String eligibility;
    private String entranceExam;

    public Course() {}

    public Course(String courseName, String specialization, String duration,
                  int intake, double fees, String eligibility, String entranceExam) {
        this.courseName = courseName;
        this.specialization = specialization;
        this.duration = duration;
        this.intake = intake;
        this.fees = fees;
        this.eligibility = eligibility;
        this.entranceExam = entranceExam;
    }

    public Long getId() { return id; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getIntake() { return intake; }
    public void setIntake(int intake) { this.intake = intake; }

    public double getFees() { return fees; }
    public void setFees(double fees) { this.fees = fees; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public String getEntranceExam() { return entranceExam; }
    public void setEntranceExam(String entranceExam) { this.entranceExam = entranceExam; }
}