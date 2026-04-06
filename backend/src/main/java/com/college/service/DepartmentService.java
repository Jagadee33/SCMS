package com.college.service;

import com.college.model.Department;
import com.college.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        Optional<Department> department = departmentRepository.findById(id);
        return department.orElse(null);
    }

    public Department createDepartment(Department department) {
        // Set default values if not provided
        if (department.getStatus() == null) {
            department.setStatus("Active");
        }
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, Department departmentDetails) {
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            department.setName(departmentDetails.getName());
            department.setCode(departmentDetails.getCode());
            department.setDescription(departmentDetails.getDescription());
            department.setHeadOfDepartment(departmentDetails.getHeadOfDepartment());
            department.setBuilding(departmentDetails.getBuilding());
            department.setFloor(departmentDetails.getFloor());
            department.setPhone(departmentDetails.getPhone());
            department.setEmail(departmentDetails.getEmail());
            department.setStatus(departmentDetails.getStatus());
            return departmentRepository.save(department);
        }
        return null;
    }

    public boolean deleteDepartment(Long id) {
        if (departmentRepository.existsById(id)) {
            departmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
