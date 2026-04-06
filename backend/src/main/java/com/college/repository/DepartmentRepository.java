package com.college.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.model.Department;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
