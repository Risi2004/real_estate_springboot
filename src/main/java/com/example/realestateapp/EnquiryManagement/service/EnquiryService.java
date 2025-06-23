package com.example.realestateapp.EnquiryManagement.service;

import com.example.realestateapp.EnquiryManagement.model.Enquiry;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnquiryService {

    private final String filePath = "data/enquiry.txt";

    public void saveEnquiry(Enquiry enquiry) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(enquiry.getFirstName() + "," +
                    enquiry.getLastName() + "," +
                    enquiry.getEmail() + "," +
                    enquiry.getPhone() + "," +
                    enquiry.getMessage() + "," +
                    enquiry.getStatus()); // Add status
            writer.newLine();
        }
    }

    public List<Enquiry> loadEnquiries() throws IOException {
        List<Enquiry> enquiries = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return enquiries;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6); // limit to 6 parts
                if (parts.length == 6) {
                    Enquiry enquiry = new Enquiry(
                            parts[0], // firstName
                            parts[1], // lastName
                            parts[2], // email
                            parts[3], // phone
                            parts[4], // message
                            parts[5]  // status
                    );
                    enquiries.add(enquiry);
                }
            }
        }

        return enquiries;
    }
}
