package com.example.learningapp.repository;

import com.example.learningapp.model.LearningClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningClassRepository extends JpaRepository<LearningClass, Long> {
}