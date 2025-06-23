package com.example.realestateapp.ReviewManagement.service;

import com.example.realestateapp.ReviewManagement.model.Review;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    private final String filePath = "data/reviews.txt";

    /**
     * Save a review to the file.
     *
     * @param review the Review object to be saved
     * @throws IOException if file operation fails
     */
    public void saveReview(Review review) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // Ensure parent folder exists

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(review.getName() + "," +
                    review.getDate() + "," +
                    review.getRating() + "," +
                    review.getComment());
            writer.newLine();
        }
    }

    /**
     * Load all reviews from the file.
     *
     * @return List of Review objects
     * @throws IOException if file read fails
     */
    public List<Review> loadReviews() throws IOException {
        List<Review> reviews = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return reviews;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4); // Ensure 4 parts (name, date, rating, comment)
                if (parts.length == 4) {
                    reviews.add(new Review(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        }

        return reviews;
    }

    public List<Review> getAllReviews() {
        try {
            return loadReviews();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
