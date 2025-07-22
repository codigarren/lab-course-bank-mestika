package com.example.learningapp.service;

import com.example.learningapp.model.Material;
import com.example.learningapp.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MaterialService {
    
    @Autowired
    private MaterialRepository materialRepository;
    
    public List<Material> findByModuleId(Long moduleId) {
        return materialRepository.findByModuleIdOrderBySequenceOrder(moduleId);
    }
    
    public Material findById(Long id) {
        return materialRepository.findById(id).orElse(null);
    }
}