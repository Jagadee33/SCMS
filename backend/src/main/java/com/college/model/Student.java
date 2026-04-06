package com.college.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String phone;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String role;

    @Column(nullable = true)
    private String course;

    @Column(nullable = true)
    private String department;

    @Column(nullable = true)
    private String specialization;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(nullable = true)
    private Double gpa;

    @Column(nullable = true)
    private String status;

    @Column(nullable = true)
    private String semester;

    @Column(nullable = true)
    private Double fees;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Student() {}

    public Student(String firstName, String lastName, String email, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    public Student(Long id, String firstName, String lastName, String email, String phone, 
                String address, LocalDate dateOfBirth, String role, String course, 
                String department, String specialization, LocalDate enrollmentDate, 
                Integer graduationYear, Double gpa, String status, String semester, 
                Double fees, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
        this.course = course;
        this.department = department;
        this.specialization = specialization;
        this.enrollmentDate = enrollmentDate;
        this.graduationYear = graduationYear;
        this.gpa = gpa;
        this.status = status;
        this.semester = semester;
        this.fees = fees;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }

    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public Double getFees() { return fees; }
    public void setFees(Double fees) { this.fees = fees; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
