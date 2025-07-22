package com.example.learningapp.repository;

import com.example.learningapp.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    @Query(value = "SELECT * FROM modules WHERE class_id = ?1 ORDER BY sequence_order", nativeQuery = true)
    List<Module> findByClassIdOrderBySequenceOrder(Long classId);
}