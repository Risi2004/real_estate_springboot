package com.example.realestateapp.BookingManagement.service;

import com.example.realestateapp.BookingManagement.model.ContactRequest;
import com.example.realestateapp.MailManagement.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class ContactRequestService {

    private final String contactFile = "data/contact.txt";
    private final String userFile = "data/users.txt"; // To get owner's email

    @Autowired
    private MailService mailService;

    public void saveContactRequest(ContactRequest request) throws IOException {
        File file = new File(contactFile);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(
                    request.getVisitorName() + "," +
                            request.getDate() + "," +
                            request.getTimeSlot() + "," +
                            request.getContactNumber() + "," +
                            request.getMessage().replace(",", ";") + "," +
                            request.getPropertyLocation() + "," +
                            request.getOwner() + "," +
                            request.isRead()
            );
            writer.newLine();
        }

        String ownerEmail = getEmailByUsername(request.getOwner());
        if (ownerEmail != null) {
            String subject = "New Booking Request for Your Property";
            String body = "Hello " + request.getOwner() + ",\n\n"
                    + "You have received a new booking/contact request for your property at:\n"
                    + request.getPropertyLocation() + "\n\n"
                    + "Details:\n"
                    + "Visitor Name: " + request.getVisitorName() + "\n"
                    + "Date: " + request.getDate() + "\n"
                    + "Time Slot: " + request.getTimeSlot() + "\n"
                    + "Contact Number: " + request.getContactNumber() + "\n"
                    + "Message: " + request.getMessage() + "\n\n"
                    + "Please respond accordingly.\n\nReal Estate App Team";

            mailService.sendGeneralNotificationEmail(ownerEmail, subject, body);
        }
    }

    public List<ContactRequest> getRequestsForOwner(String owner) {
        List<ContactRequest> requests = new ArrayList<>();
        File file = new File(contactFile);
        if (!file.exists()) return requests;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 8);
                if (parts.length == 8 && parts[6].equals(owner)) {
                    requests.add(new ContactRequest(
                            parts[0], parts[1], parts[2], parts[3], parts[4].replace(";", ","),
                            parts[5], parts[6], Boolean.parseBoolean(parts[7])
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public int getUnreadCount(String owner) {
        return (int) getRequestsForOwner(owner).stream()
                .filter(req -> !req.isRead())
                .count();
    }

    public void markAllAsRead(String owner) {
        File file = new File(contactFile);
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 8);
                if (parts.length == 8 && parts[6].equals(owner) && parts[7].equals("false")) {
                    parts[7] = "true";
                }
                updatedLines.add(String.join(",", parts));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : updatedLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void markSingleRequestAsRead(String name, String date, String timeSlot, String owner) throws IOException {
        File file = new File(contactFile);
        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 8);
                if (parts.length == 8 && parts[0].equals(name) && parts[1].equals(date)
                        && parts[2].equals(timeSlot) && parts[6].equals(owner)) {
                    parts[7] = "true";
                }
                updatedLines.add(String.join(",", parts));
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String l : updatedLines) {
                writer.write(l);
                writer.newLine();
            }
        }
    }

    private String getEmailByUsername(String username) {
        File file = new File(userFile);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    return parts[2]; // email
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
