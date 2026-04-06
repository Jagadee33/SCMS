package com.college.service;

import com.college.model.Course;
import com.college.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.orElse(null);
    }

    public Course createCourse(Course course) {
        // Set default values if not provided
        if (course.getStatus() == null) {
            course.setStatus("Active");
        }
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setName(courseDetails.getName());
            course.setCode(courseDetails.getCode());
            course.setDescription(courseDetails.getDescription());
            course.setDepartment(courseDetails.getDepartment());
            course.setInstructor(courseDetails.getInstructor());
            course.setCredits(courseDetails.getCredits());
            course.setDuration(courseDetails.getDuration());
            course.setStartDate(courseDetails.getStartDate());
            course.setEndDate(courseDetails.getEndDate());
            course.setStatus(courseDetails.getStatus());
            return courseRepository.save(course);
        }
        return null;
    }

    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
