package com.example.realestateapp.EnquiryManagement.model;

import lombok.Data;

@Data

public class Enquiry {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String message;
    private String status;

    public Enquiry(String firstName, String lastName, String email, String phone, String message) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.message = message;
        this.status = "Pending";
    }

    public Enquiry(String firstName, String lastName, String email, String phone, String message, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.message = message;
        this.status = status;
    }

//    public String getFirstName() { return firstName; }
//    public String getLastName() { return lastName; }
//    public String getEmail() { return email; }
//    public String getPhone() { return phone; }
//    public String getMessage() { return message; }
//    public String getStatus() { return status; }
}
