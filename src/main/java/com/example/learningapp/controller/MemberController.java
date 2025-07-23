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
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

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
        //Start
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            session.invalidate();
            return "redirect:/login";
        }
        //End
        List<LearningClass> classes = learningClassService.findAll();
        List<Purchase> purchases = purchaseService.findByUserId(userId);

        model.addAttribute("classes", classes);
        model.addAttribute("purchases", purchases);
        return "member/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        //Start
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            return "redirect:/login";
        }
        //End
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
        //Start
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            session.invalidate();
            return "redirect:/login";
        }
        //End
        User user = userService.findById(userId);

        String cleanedFullName = Jsoup.clean(fullName, Safelist.basic());
        String cleanedEmail = Jsoup.clean(email, Safelist.basic());
        String cleanedPhone = Jsoup.clean(phone, Safelist.basic());

        // Vulnerable: No input validation
        user.setFullName(cleanedFullName);
        user.setEmail(cleanedEmail);
        user.setPhone(cleanedPhone);

        userService.save(user);
        model.addAttribute("success", "Profile updated successfully");
        model.addAttribute("user", user);
        return "member/profile";
    }

    @GetMapping("/classes")
    public String classes(Model model) {
        //Start
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            session.invalidate();
            return "redirect:/login";
        }
        //End
        List<LearningClass> classes = learningClassService.findAll();
        model.addAttribute("classes", classes);
        return "member/classes";
    }

    @GetMapping("/class/{id}")
    public String classDetail(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        //Start
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            session.invalidate();
            return "redirect:/login";
        }
        //End
        LearningClass learningClass = learningClassService.findById(id);
        List<com.example.learningapp.model.Module> modules = moduleService.findByClassId(id);
        List<Feedback> feedbacks = feedbackService.findByClassId(id);
        //Start
        for (Feedback feedback : feedbacks) {
            String originalComment = feedback.getComment();
            String sanitizedComment = Jsoup.clean(originalComment, Safelist.basic());
            feedback.setComment(sanitizedComment);
        }
        //End
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

        //Start
        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            session.invalidate();
            return "redirect:/login";
        }

        Purchase purchase = purchaseService.findConfirmedPurchase(userId, classId);
        if (purchase == null) {
            model.addAttribute("error", "You do not have access to this material.");
            return "error/403";
        }
        //End

        // Should check if user has purchased the class, but doesn't
        Material material = materialService.findById(materialId);
        //Start
        String originalContent = feedback.getContent();
        String cleanedContent = Jsoup.clean(originalContent, Safelist.basic()); // atau Safelist.none()
        material.setContent(cleanedContent);
        //End
        com.example.learningapp.model.Module module = moduleService.findById(moduleId);
        LearningClass learningClass = learningClassService.findById(classId);

        model.addAttribute("material", material);
        model.addAttribute("module", module);
        model.addAttribute("class", learningClass);
        return "member/material-detail";
    }

    @GetMapping("/checkout/{classId}")
    public String checkout(@PathVariable Long classId, Model model) {
        //Start
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            session.invalidate();
            return "redirect:/login";
        }
        //End
        LearningClass learningClass = learningClassService.findById(classId);
        model.addAttribute("class", learningClass);
        return "member/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@RequestParam Long classId,
            @RequestParam BigDecimal price,
            HttpSession session,
            Model model) {
        //Start
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            session.invalidate();
            return "redirect:/login";
        }
        //End

        try {
            Long userId = (Long) session.getAttribute("userId");
            
            if (userId == null) {
                return "redirect:/login";
            }

            System.out.println("Processing checkout - UserId: " + userId + ", ClassId: " + classId + ", Price: " + price);

            // Vulnerable: Price manipulation - trusting client-side price
            //Start
            LearningClass learningClass = learningClassService.findById(classId);
            //End

            Purchase purchase = new Purchase();
            purchase.setUserId(userId);
            purchase.setClassId(classId);
            purchase.setPrice(learningClass.getPrice());
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
        //Start
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            session.invalidate();
            return "redirect:/login";
        }
        //End
        System.out.println("Upload payment - PurchaseId: " + purchaseId);
        System.out.println("File empty: " + file.isEmpty());
        System.out.println("File name: " + file.getOriginalFilename());

        try {
            // Vulnerable: No file validation
            if (!file.isEmpty()) {
                //Start
                String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
                String extension = FilenameUtils.getExtension(originalFileName).toLowerCase();
                String mimeType = file.getContentType();
                long fileSize = file.getSize();
            
                // Valid extensions
                List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "pdf");
                // Valid MIME types
                List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png", "application/pdf");
                // Max file size (in bytes)
                long maxFileSize = 5 * 1024 * 1024;
            
                // Validate extension
                if (!allowedExtensions.contains(extension)) {
                    model.addAttribute("error", "File type not allowed");
                    return "member/payment-upload";
                }
            
                // Validate MIME type
                if (!allowedMimeTypes.contains(mimeType)) {
                    model.addAttribute("error", "Invalid MIME type");
                    return "member/payment-upload";
                }
            
                // Validate file size
                if (fileSize > maxFileSize) {
                    model.addAttribute("error", "File too large (max 2MB)");
                    return "member/payment-upload";
                }
                //End

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

        //Start
        String role = (String) session.getAttribute("role");
        if (role == null || !role.equals("MEMBER")) {
            session.invalidate();
            return "redirect:/login";
        }

        String cleanedComment = Jsoup.clean(comment, Safelist.basic());
        //End

        Long userId = (Long) session.getAttribute("userId");

        // Vulnerable: No XSS protection
        Feedback feedback = new Feedback(userId, classId, rating, cleanedComment);
        feedbackService.save(feedback);

        return "redirect:/member/class/" + classId;
    }
}
