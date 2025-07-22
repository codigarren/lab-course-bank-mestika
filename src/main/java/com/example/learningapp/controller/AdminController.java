package com.example.learningapp.controller;

import com.example.learningapp.model.*;
import com.example.learningapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private LearningClassService learningClassService;

    @Autowired
    private PurchaseService purchaseService;

    // Vulnerable: No authorization check
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        try {
            // Check if user is logged in and is admin
            String role = (String) session.getAttribute("role");
            if (role == null || !role.equals("ADMIN")) {
                return "redirect:/login";
            }

            System.out.println("Admin dashboard access - Role: " + role);

            List<LearningClass> classes = learningClassService.findAll();
            List<Purchase> purchases = purchaseService.findAll();

            System.out.println("Classes found: " + (classes != null ? classes.size() : "null"));
            System.out.println("Purchases found: " + (purchases != null ? purchases.size() : "null"));

            model.addAttribute("classes", classes != null ? classes : new ArrayList<>());
            model.addAttribute("purchases", purchases != null ? purchases : new ArrayList<>());

            return "admin/dashboard";

        } catch (Exception e) {
            System.out.println("Admin dashboard error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/classes")
    public String classes(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("ADMIN")) {
            return "redirect:/login";
        }

        List<LearningClass> classes = learningClassService.findAll();
        model.addAttribute("classes", classes != null ? classes : new ArrayList<>());
        return "admin/classes";
    }

    @GetMapping("/purchases")
    public String purchases(HttpSession session, Model model) {
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("ADMIN")) {
            return "redirect:/login";
        }

        List<Purchase> purchases = purchaseService.findAll();
        model.addAttribute("purchases", purchases != null ? purchases : new ArrayList<>());
        return "admin/purchases";
    }

    @GetMapping("/purchase/{id}")
    public String purchaseDetail(@PathVariable Long id, Model model) {
        Purchase purchase = purchaseService.findById(id);
        model.addAttribute("purchase", purchase);
        return "admin/purchase-detail";
    }

    @PostMapping("/purchase/update-status")
    public String updatePurchaseStatus(@RequestParam Long purchaseId,
            @RequestParam String status,
            Model model) {

        Purchase.Status newStatus = Purchase.Status.valueOf(status);
        purchaseService.updateStatus(purchaseId, newStatus);

        return "redirect:/admin/purchases";
    }
}
