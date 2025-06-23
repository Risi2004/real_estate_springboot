package com.example.realestateapp.Newsletter.controller;

import com.example.realestateapp.Newsletter.model.NewsletterSubscription;
import com.example.realestateapp.Newsletter.service.NewsletterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class NewsletterController {

    @Autowired
    private NewsletterService newsletterService;

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam String email) {
        try {
            NewsletterSubscription subscription = new NewsletterSubscription(firstName, lastName, email);
            newsletterService.saveSubscription(subscription);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/home";
    }

    // ✅ NEW: Endpoint to view all newsletter data
    @GetMapping("/admin_newsletter")
    public String showSubscriptions(Model model) {
        try {
            List<NewsletterSubscription> list = newsletterService.getAllSubscriptions();
            model.addAttribute("subscriptions", list);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to read subscriptions.");
        }
        return "admin_newsletter"; // Must match Thymeleaf file name
    }
}
