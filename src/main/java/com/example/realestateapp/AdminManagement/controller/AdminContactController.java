package com.example.realestateapp.AdminManagement.controller;

import com.example.realestateapp.AdminManagement.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/admin_contact")
    public String viewContactRequests(Model model) {
        model.addAttribute("contactList", contactService.getAllRequests());
        return "admin_contact"; // Thymeleaf HTML file
    }
}
