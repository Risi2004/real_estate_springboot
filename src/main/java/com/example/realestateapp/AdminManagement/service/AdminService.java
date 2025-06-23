package com.example.realestateapp.AdminManagement.service;


import com.example.realestateapp.AdminManagement.model.AdminStats;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Service
public class AdminService {

    private final String USERS_FILE = "data/users.txt";
    private final String PROPERTIES_FILE = "data/properties.txt";

    public AdminStats getDashboardStats() {
        int userCount = countLines(USERS_FILE);
        int propertyCount = countLines(PROPERTIES_FILE);
        return new AdminStats(userCount, propertyCount);
    }

    private int countLines(String filePath) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
