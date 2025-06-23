package com.example.realestateapp.ReviewManagement.controller;

import com.example.realestateapp.ReviewManagement.model.Review;
import com.example.realestateapp.ReviewManagement.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Handles the review form submission and saves to reviews.txt
    @PostMapping("/submitReview")
    public String submitReview(@RequestParam String name,
                               @RequestParam String date,
                               @RequestParam String rating,
                               @RequestParam String comment) {

        Review review = new Review(name, date, rating, comment);

        try {
            reviewService.saveReview(review);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Redirect back to home page where reviews will be shown
        return "redirect:/home";
    }

}
