package com.college.service;

import com.college.model.Faculty;
import com.college.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    public Faculty getFacultyById(Long id) {
        Optional<Faculty> faculty = facultyRepository.findById(id);
        return faculty.orElse(null);
    }

    public Faculty createFaculty(Faculty faculty) {
        // Set default values if not provided
        if (faculty.getStatus() == null) {
            faculty.setStatus("Active");
        }
        if (faculty.getHireDate() == null) {
            faculty.setHireDate(LocalDate.now());
        }
        return facultyRepository.save(faculty);
    }

    public Faculty updateFaculty(Long id, Faculty facultyDetails) {
        Optional<Faculty> optionalFaculty = facultyRepository.findById(id);
        if (optionalFaculty.isPresent()) {
            Faculty faculty = optionalFaculty.get();
            faculty.setFirstName(facultyDetails.getFirstName());
            faculty.setLastName(facultyDetails.getLastName());
            faculty.setEmail(facultyDetails.getEmail());
            faculty.setPhone(facultyDetails.getPhone());
            faculty.setDepartment(facultyDetails.getDepartment());
            faculty.setSpecialization(facultyDetails.getSpecialization());
            faculty.setSalary(facultyDetails.getSalary());
            faculty.setStatus(facultyDetails.getStatus());
            if (facultyDetails.getHireDate() != null) {
                faculty.setHireDate(facultyDetails.getHireDate());
            }
            return facultyRepository.save(faculty);
        }
        return null;
    }

    public boolean deleteFaculty(Long id) {
        if (facultyRepository.existsById(id)) {
            facultyRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
