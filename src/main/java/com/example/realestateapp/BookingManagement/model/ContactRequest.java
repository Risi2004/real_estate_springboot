package com.example.realestateapp.BookingManagement.model;

import lombok.Data;
@Data


public class ContactRequest extends Request {
    private String contactNumber;
    private String message;
    private String propertyLocation;
    private String owner;
    private boolean isRead;

    public ContactRequest(String visitorName, String date, String timeSlot, String contactNumber, String message, String propertyLocation, String owner, boolean isRead) {
        super(visitorName, date, timeSlot);
        this.contactNumber = contactNumber;
        this.message = message;
        this.propertyLocation = propertyLocation;
        this.owner = owner;
        this.isRead = isRead;
    }
}
