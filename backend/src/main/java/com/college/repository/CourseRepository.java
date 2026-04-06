package com.college.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.model.Course;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
