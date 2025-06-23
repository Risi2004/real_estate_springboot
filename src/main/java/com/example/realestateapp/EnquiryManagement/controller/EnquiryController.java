package com.example.realestateapp.EnquiryManagement.controller;

import com.example.realestateapp.EnquiryManagement.model.Enquiry;
import com.example.realestateapp.EnquiryManagement.service.EnquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EnquiryController {

    @Autowired
    private EnquiryService enquiryService;

    @PostMapping("/submitEnquiry")
    public String handleEnquiry(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam String message) {
        try {
            Enquiry enquiry = new Enquiry(firstName, lastName, email, phone, message, "Pending");
            enquiryService.saveEnquiry(enquiry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/contact";
    }

    @GetMapping("/admin_enquiry")
    public String viewEnquiries(Model model) {
        try {
            List<Enquiry> enquiries = enquiryService.loadEnquiries();
            model.addAttribute("enquiries", enquiries);
        } catch (Exception e) {
            model.addAttribute("enquiries", List.of());
            e.printStackTrace();
        }
        return "admin_enquiry"; // points to templates/admin-enquiries.html
    }
}
