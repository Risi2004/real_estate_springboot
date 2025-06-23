package com.example.realestateapp.AdminManagement.controller;

import com.example.realestateapp.PropertyManagement.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class AdminPropertyController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/admin_properties")
    public String showAllProperties(Model model) {
        model.addAttribute("propertyList", propertyService.getAllProperties());
        return "admin_properties";
    }

    @PostMapping("/admin_properties/delete")
    public String deleteProperty(@RequestParam String owner,
                                 @RequestParam String location) {
        try {
            propertyService.deleteProperty(owner, location);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin_properties";
    }
}
