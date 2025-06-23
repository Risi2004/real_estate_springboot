package com.example.realestateapp.AdminManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ContactRequest {
    private String name;
    private String timeSlot;
    private String phone;
    private String message;
    private String location;
    private String owner;

//    public ContactRequest(String name, String timeSlot, String phone, String message, String location, String owner) {
//        this.name = name;
//        this.timeSlot = timeSlot;
//        this.phone = phone;
//        this.message = message;
//        this.location = location;
//        this.owner = owner;
//    }
//
//    public String getName() { return name; }
//    public String getTimeSlot() { return timeSlot; }
//    public String getPhone() { return phone; }
//    public String getMessage() { return message; }
//    public String getLocation() { return location; }
//    public String getOwner() { return owner; }
}
