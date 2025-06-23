package com.example.realestateapp.Newsletter.service;

import com.example.realestateapp.Newsletter.model.NewsletterSubscription;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsletterService {

    private final String filePath = "data/newsletters_subscription.txt";

    // Save a new subscription
    public void saveSubscription(NewsletterSubscription subscription) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // Ensure directory exists

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(subscription.getFirstName() + "," + subscription.getLastName() + "," + subscription.getEmail());
            writer.newLine();
        }
    }

    // Get all subscriptions
    public List<NewsletterSubscription> getAllSubscriptions() throws IOException {
        List<NewsletterSubscription> subscriptions = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) return subscriptions;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    NewsletterSubscription subscription = new NewsletterSubscription(parts[0], parts[1], parts[2]);
                    subscriptions.add(subscription);
                }
            }
        }

        return subscriptions;
    }



    // ✅ Delete a subscription by email (called when user is deleted)
    public void deleteSubscriptionByEmail(String emailToDelete) throws IOException {
        File file = new File(filePath);
        List<String> updatedLines = new ArrayList<>();

        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3 && !parts[2].equalsIgnoreCase(emailToDelete)) {
                    updatedLines.add(line);
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        }
    }
}
