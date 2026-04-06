package com.college.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.model.Faculty;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
}
