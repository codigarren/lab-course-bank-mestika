package com.example.learningapp.service;

import com.example.learningapp.model.Feedback;
import com.example.learningapp.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
    
    public List<Feedback> findByClassId(Long classId) {
        return feedbackRepository.findByClassId(classId);
    }
}