package com.example.learningapp.service;

import com.example.learningapp.model.Purchase;
import com.example.learningapp.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseService {
    
    @Autowired
    private PurchaseRepository purchaseRepository;
    
    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }
    
    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }
    
    public List<Purchase> findByUserId(Long userId) {
        return purchaseRepository.findByUserId(userId);
    }
    
    public Purchase findById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }
    
    public Purchase findConfirmedPurchase(Long userId, Long classId) {
        return purchaseRepository.findConfirmedPurchase(userId, classId);
    }
    
    public Purchase updateStatus(Long id, Purchase.Status status) {
        Purchase purchase = findById(id);
        if (purchase != null) {
            purchase.setStatus(status);
            if (status == Purchase.Status.CONFIRMED) {
                purchase.setConfirmedAt(LocalDateTime.now());
            }
            return save(purchase);
        }
        return null;
    }
}