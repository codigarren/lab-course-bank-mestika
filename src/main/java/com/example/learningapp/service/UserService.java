package com.example.learningapp.service;

import com.example.learningapp.model.User;
import com.example.learningapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.*;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User authenticate(String username, String password) {
        System.out.println("Authenticating user: " + username);
        System.out.println("Input password: " + password);
        
        User user = userRepository.findByUsername(username);
        if (user != null) {
            System.out.println("User found: " + user.getUsername());
            System.out.println("Stored password: " + user.getPassword());
            System.out.println("Password match: " + user.getPassword().equals(password));
            
            if (user.getPassword().equals(password)) {
                return user;
            }
        } else {
            System.out.println("User not found");
        }
        return null;
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
