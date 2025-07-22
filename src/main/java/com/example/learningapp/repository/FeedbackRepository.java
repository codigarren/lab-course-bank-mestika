package com.example.learningapp.repository;

import com.example.learningapp.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    @Query(value = "SELECT * FROM feedback WHERE class_id = ?1", nativeQuery = true)
    List<Feedback> findByClassId(Long classId);
}