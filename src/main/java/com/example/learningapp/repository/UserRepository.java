package com.example.learningapp.repository;

import com.example.learningapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Vulnerable: Direct SQL injection possible
    // @Query(value = "SELECT * FROM users WHERE username = ?1 AND password = ?2", nativeQuery = true)
    // User findByUsernameAndPassword(String username, String password);
    // findByUsernameAndPassword -> tidak dipakai?
    
    User findByUsername(String username);
    User findByEmail(String email);
}