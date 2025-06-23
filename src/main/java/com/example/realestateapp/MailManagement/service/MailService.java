package com.example.realestateapp.MailManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Get user email by username from users.txt
    public String getEmailByUsername(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    return parts[2]; // username,password,email
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Signup email
    public void sendSignupEmail(String to, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Welcome to Real Estate App – Your Account Has Been Created!");
            message.setText("Hi " + username + ",\n\n" +
                    "Thank you for registering with Real Estate App!\n\n" +
                    "Your account has been successfully created. You can now browse, list, and manage properties with ease. We’re excited to have you on board!\n\n" +
                    "If you have any questions or need assistance, feel free to contact our support team.\n\n" +
                    "Best regards,\nReal Estate App Team");

            mailSender.send(message);
            System.out.println("✅ Signup email sent to " + to);
        } catch (Exception e) {
            System.out.println("❌ Failed to send signup email to " + to);
            e.printStackTrace();
        }
    }

    // ✅ Login notification email
    public void sendLoginEmail(String to, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Login Notification");
            message.setText("Hi " + username + ",\n\nYou have successfully logged in to the Real Estate App.\n\nRegards,\nReal Estate Team");

            mailSender.send(message);
            System.out.println("✅ Login email sent to " + to);
        } catch (Exception e) {
            System.out.println("❌ Failed to send login email to " + to);
            e.printStackTrace();
        }
    }

    // ✅ User update email
    public void sendUserUpdateEmail(String to, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Profile Updated");
            message.setText("Hi " + username + ",\n\nYour profile details were successfully updated.\n\nRegards,\nReal Estate App Team");

            mailSender.send(message);
            System.out.println("✅ User update email sent to " + to);
        } catch (Exception e) {
            System.out.println("❌ Failed to send user update email to " + to);
            e.printStackTrace();
        }
    }

    // ✅ General purpose email
    public void sendGeneralNotificationEmail(String to, String subject, String messageBody) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText("Hi,\n\n" + messageBody + "\n\nRegards,\nReal Estate App Team");

            mailSender.send(message);
            System.out.println("✅ Email sent to " + to + " with subject: " + subject);
        } catch (Exception e) {
            System.out.println("❌ Failed to send general notification email to " + to);
            e.printStackTrace();
        }
    }

    // ✅ Payment receipt email
    public void sendPaymentReceipt(String username, String receiptText) {
        String to = getEmailByUsername(username);
        if (to == null) {
            System.out.println("❌ Email not found for user: " + username);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Payment Receipt – Real Estate App");
            message.setText("Hi " + username + ",\n\n" +
                    "Thank you for your recent payment on Real Estate App. Here are your transaction details:\n\n" +
                    "------------------------------------------\n" +
                    receiptText +
                    "------------------------------------------\n\n" +
                    "This receipt confirms that your payment was successfully processed. If you have any questions, feel free to reach out.\n\n" +
                    "Best regards,\nReal Estate App Billing Team");

            mailSender.send(message);
            System.out.println("✅ Payment receipt email sent to " + to);
        } catch (Exception e) {
            System.out.println("❌ Failed to send payment receipt to " + to);
            e.printStackTrace();
        }
    }

    // ✅ Password reset link email
    public void sendResetLink(String to, String token) {
        try {
            String resetUrl = "http://localhost:8080/reset-password?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Reset Your Password – Real Estate App");
            message.setText("Hi,\n\n" +
                    "We received a request to reset your password. Click the link below to set a new password:\n\n" +
                    resetUrl + "\n\n" +
                    "If you didn’t request this, you can ignore this email.\n\n" +
                    "Best regards,\nReal Estate App Team");

            mailSender.send(message);
            System.out.println("✅ Password reset email sent to " + to);
        } catch (Exception e) {
            System.out.println("❌ Failed to send password reset email to " + to);
            e.printStackTrace();
        }
    }

    // ✅ Booking contact request notification to property owner
    public void sendBookingNotificationToOwner(String ownerUsername, String propertyLocation, String visitorName,
                                               String date, String timeSlot, String contactNumber, String messageText) {
        String to = getEmailByUsername(ownerUsername);
        if (to == null) {
            System.out.println("❌ Email not found for property owner: " + ownerUsername);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("New Booking Request for Your Property");
            message.setText("Hello " + ownerUsername + ",\n\n"
                    + "You have received a new booking/contact request for your property at:\n"
                    + propertyLocation + "\n\n"
                    + "Visitor Details:\n"
                    + "Name: " + visitorName + "\n"
                    + "Date: " + date + "\n"
                    + "Time Slot: " + timeSlot + "\n"
                    + "Contact Number: " + contactNumber + "\n"
                    + "Message: " + messageText + "\n\n"
                    + "Please follow up accordingly.\n\n"
                    + "Regards,\nReal Estate App Team");

            mailSender.send(message);
            System.out.println("✅ Booking email sent to " + to);
        } catch (Exception e) {
            System.out.println("❌ Failed to send booking email to " + to);
            e.printStackTrace();
        }
    }
}
