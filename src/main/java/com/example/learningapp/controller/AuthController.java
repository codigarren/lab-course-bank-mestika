package com.example.learningapp.controller;

import com.example.learningapp.model.User;
import com.example.learningapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        System.out.println("Login attempt - Username: " + username + ", Password: " + password);
        
        // Vulnerable: No input validation/sanitization
        User user = userService.authenticate(username, password);
        
        System.out.println("Authentication result: " + (user != null ? "SUCCESS" : "FAILED"));

        if (user != null) {
            System.out.println("User authenticated: " + user.getUsername() + ", Role: " + user.getRole());
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().toString());
            session.setAttribute("fullName", user.getFullName());

            if (user.getRole() == User.Role.ADMIN) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/member/dashboard";
            }
        } else {
            System.out.println("Login failed for username: " + username);
            model.addAttribute("error", "Invalid username or password");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam(defaultValue = "MEMBER") String role,
            Model model) {

        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "Username already exists");
            return "auth/register";
        }

        if (userService.findByEmail(email) != null) {
            model.addAttribute("error", "Email already exists");
            return "auth/register";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // Simpan sebagai plaintext
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRole(User.Role.valueOf(role));

        System.out.println("Registering user: " + username + " with password: " + password);
        userService.save(user);

        model.addAttribute("success", "Registration successful! Please login.");
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
