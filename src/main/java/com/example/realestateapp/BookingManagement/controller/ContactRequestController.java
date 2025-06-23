
package com.example.realestateapp.BookingManagement.controller;

import com.example.realestateapp.BookingManagement.model.ContactRequest;
import com.example.realestateapp.BookingManagement.service.ContactRequestService;
import com.example.realestateapp.MailManagement.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class ContactRequestController {

    @Autowired
    private ContactRequestService contactService;

    @Autowired
    private MailService mailService;

    @GetMapping("/contact-owner")
    public String contactOwnerForm(@RequestParam String owner,
                                   @RequestParam String location,
                                   HttpSession session,
                                   Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null || username.equals(owner)) return "redirect:/sales";

        model.addAttribute("owner", owner);
        model.addAttribute("propertyLocation", location);
        model.addAttribute("username", username);
        return "contact-owner";
    }

    @PostMapping("/contact-owner")
    public String submitContact(@RequestParam String name,
                                @RequestParam String date,
                                @RequestParam String timeSlot,
                                @RequestParam String contactNo,
                                @RequestParam String message,
                                @RequestParam String propertyLocation,
                                @RequestParam String owner) {
        try {
            ContactRequest request = new ContactRequest(
                    name, date, timeSlot, contactNo, message, propertyLocation, owner, false
            );
            contactService.saveContactRequest(request);

            // Optional: Already handled in service, but keep this if your logic depends on it
            mailService.sendBookingNotificationToOwner(
                    owner, propertyLocation, name, date, timeSlot, contactNo, message
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/sales";
    }

    @GetMapping("/contact-requests")
    public String viewRequests(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        model.addAttribute("username", username);
        model.addAttribute("requests", contactService.getRequestsForOwner(username));
        return "contact-requests";
    }

    @PostMapping("/mark-contact-read")
    public String markContactRead(@RequestParam String name,
                                  @RequestParam String date,
                                  @RequestParam String timeSlot,
                                  @RequestParam String owner) {
        try {
            contactService.markSingleRequestAsRead(name, date, timeSlot, owner);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/contact-requests";
    }

    @GetMapping("/get-unread-contact-count")
    @ResponseBody
    public int getUnreadContactCount(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return 0;
        return contactService.getUnreadCount(username);
    }
}





