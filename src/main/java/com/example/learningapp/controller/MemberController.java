package com.example.learningapp.controller;

import com.example.learningapp.model.*;
import com.example.learningapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private UserService userService;

    @Autowired
    private LearningClassService learningClassService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Vulnerable: No session validation
        Long userId = (Long) session.getAttribute("userId");
        List<LearningClass> classes = learningClassService.findAll();
        List<Purchase> purchases = purchaseService.findByUserId(userId);

        model.addAttribute("classes", classes);
        model.addAttribute("purchases", purchases);
        return "member/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "member/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);

        // Vulnerable: No input validation
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);

        userService.save(user);
        model.addAttribute("success", "Profile updated successfully");
        model.addAttribute("user", user);
        return "member/profile";
    }

    @GetMapping("/classes")
    public String classes(Model model) {
        List<LearningClass> classes = learningClassService.findAll();
        model.addAttribute("classes", classes);
        return "member/classes";
    }

    @GetMapping("/class/{id}")
    public String classDetail(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        LearningClass learningClass = learningClassService.findById(id);
        List<com.example.learningapp.model.Module> modules = moduleService.findByClassId(id);
        List<Feedback> feedbacks = feedbackService.findByClassId(id);
        Purchase purchase = purchaseService.findConfirmedPurchase(userId, id);

        model.addAttribute("class", learningClass);
        model.addAttribute("modules", modules);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("hasPurchased", purchase != null);
        return "member/class-detail";
    }

    // Vulnerable: No access control
    @GetMapping("/material/{classId}/{moduleId}/{materialId}")
    public String materialDetail(@PathVariable Long classId,
            @PathVariable Long moduleId,
            @PathVariable Long materialId,
            HttpSession session,
            Model model) {

        // Should check if user has purchased the class, but doesn't
        Material material = materialService.findById(materialId);
        com.example.learningapp.model.Module module = moduleService.findById(moduleId);
        LearningClass learningClass = learningClassService.findById(classId);

        model.addAttribute("material", material);
        model.addAttribute("module", module);
        model.addAttribute("class", learningClass);
        return "member/material-detail";
    }

    @GetMapping("/checkout/{classId}")
    public String checkout(@PathVariable Long classId, Model model) {
        LearningClass learningClass = learningClassService.findById(classId);
        model.addAttribute("class", learningClass);
        return "member/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam Long classId,
            @RequestParam BigDecimal price,
            HttpSession session,
            Model model) {

        try {
            Long userId = (Long) session.getAttribute("userId");
            
            if (userId == null) {
                return "redirect:/login";
            }

            System.out.println("Processing checkout - UserId: " + userId + ", ClassId: " + classId + ", Price: " + price);

            // Vulnerable: Price manipulation - trusting client-side price
            Purchase purchase = new Purchase();
            purchase.setUserId(userId);
            purchase.setClassId(classId);
            purchase.setPrice(price);
            purchase.setStatus(Purchase.Status.PENDING);
            
            purchase = purchaseService.save(purchase);

            model.addAttribute("success", "Order placed successfully! Please upload payment proof.");
            model.addAttribute("purchaseId", purchase.getId());
            return "member/payment-upload";
            
        } catch (Exception e) {
            System.out.println("Checkout error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to process checkout: " + e.getMessage());
            return "member/checkout";
        }
    }

    @PostMapping("/upload-payment")
    public String uploadPayment(@RequestParam Long purchaseId,
            @RequestParam("file") MultipartFile file,
            Model model) {

        System.out.println("Upload payment - PurchaseId: " + purchaseId);
        System.out.println("File empty: " + file.isEmpty());
        System.out.println("File name: " + file.getOriginalFilename());

        try {
            // Vulnerable: No file validation
            if (!file.isEmpty()) {
                String uploadDir = "uploads/payments/";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String fileName = file.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);
                Files.write(path, file.getBytes());

                Purchase purchase = purchaseService.findById(purchaseId);
                if (purchase != null) {
                    purchase.setPaymentProof(uploadDir + fileName);
                    purchaseService.save(purchase);
                    model.addAttribute("success", "Payment proof uploaded successfully!");
                } else {
                    model.addAttribute("error", "Purchase not found");
                }
            } else {
                model.addAttribute("error", "Please select a file to upload");
            }
        } catch (Exception e) {
            System.out.println("Upload error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to upload file: " + e.getMessage());
        }

        model.addAttribute("purchaseId", purchaseId);
        return "member/payment-upload";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam Long classId,
            @RequestParam Integer rating,
            @RequestParam String comment,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");

        // Vulnerable: No XSS protection
        Feedback feedback = new Feedback(userId, classId, rating, comment);
        feedbackService.save(feedback);

        return "redirect:/member/class/" + classId;
    }
}
