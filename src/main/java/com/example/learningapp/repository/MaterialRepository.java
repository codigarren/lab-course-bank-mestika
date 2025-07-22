package com.example.learningapp.repository;

import com.example.learningapp.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    
    @Query(value = "SELECT * FROM materials WHERE module_id = ?1 ORDER BY sequence_order", nativeQuery = true)
    List<Material> findByModuleIdOrderBySequenceOrder(Long moduleId);
}