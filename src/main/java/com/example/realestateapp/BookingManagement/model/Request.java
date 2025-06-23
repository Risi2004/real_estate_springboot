package com.example.realestateapp.BookingManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Request {
    protected String visitorName;
    protected String date;
    protected String timeSlot;
}
