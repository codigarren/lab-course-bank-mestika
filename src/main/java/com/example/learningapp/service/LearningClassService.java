package com.example.learningapp.service;

import com.example.learningapp.model.LearningClass;
import com.example.learningapp.repository.LearningClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LearningClassService {
    
    @Autowired
    private LearningClassRepository learningClassRepository;
    
    public List<LearningClass> findAll() {
        return learningClassRepository.findAll();
    }
    
    public LearningClass findById(Long id) {
        return learningClassRepository.findById(id).orElse(null);
    }
    
    public LearningClass save(LearningClass learningClass) {
        return learningClassRepository.save(learningClass);
    }
}