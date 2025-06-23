package com.example.realestateapp.AdminManagement.service;

import com.example.realestateapp.AdminManagement.model.ContactRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class ContactService {
    private final String filePath = "data/contact.txt";

    public List<ContactRequest> getAllRequests() {
        List<ContactRequest> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length == 6) {
                    // Extract just the owner name from 'w,Risi,true'
                    String[] ownerParts = parts[5].split(",");
                    String ownerName = ownerParts.length >= 2 ? ownerParts[1] : parts[5];

                    list.add(new ContactRequest(
                            parts[0], // name
                            parts[1], // time slot
                            parts[2], // phone
                            parts[3], // message
                            parts[4], // location
                            ownerName // only the owner's name
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
