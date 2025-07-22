package com.example.learningapp.repository;

import com.example.learningapp.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    
    @Query(value = "SELECT * FROM purchases WHERE user_id = ?1", nativeQuery = true)
    List<Purchase> findByUserId(Long userId);
    
    @Query(value = "SELECT * FROM purchases WHERE user_id = ?1 AND class_id = ?2 AND status = 'CONFIRMED'", nativeQuery = true)
    Purchase findConfirmedPurchase(Long userId, Long classId);
}