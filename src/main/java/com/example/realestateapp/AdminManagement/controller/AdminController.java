package com.example.realestateapp.AdminManagement.controller;

import com.example.realestateapp.PropertyManagement.service.PropertyService;
import com.example.realestateapp.UserManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(Model model) throws IOException {
        int userCount = userService.getUserCount();
        int propertyCount = propertyService.getPropertyCount();

        double totalRevenue = propertyService.calculateTotalRevenue();
        System.out.println("DEBUG: Total revenue from PropertyService = " + totalRevenue); // 👈 add this
        model.addAttribute("totalRevenue", String.format("%.2f", totalRevenue));

        model.addAttribute("userCount", userCount);
        model.addAttribute("propertyCount", propertyCount);
        model.addAttribute("userList", userService.getLatestUsers());
        model.addAttribute("propertyList", propertyService.getLatestProperties());

        return "admin-dashboard";
    }

}
