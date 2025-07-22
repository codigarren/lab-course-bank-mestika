package com.example.learningapp.service;

import com.example.learningapp.model.Module;
import com.example.learningapp.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ModuleService {
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    public List<Module> findByClassId(Long classId) {
        return moduleRepository.findByClassIdOrderBySequenceOrder(classId);
    }
    
    public Module findById(Long id) {
        return moduleRepository.findById(id).orElse(null);
    }
}