//package com.example.realestateapp.PropertyManagement.model;
//
//public class Property {
//    private String owner;
//    private String location;
//    private String country;
//    private double price;
//    private String type;
//    private String size;
//    private String imageUrl;
//
//    public Property() {}
//
//    public Property(String owner, String location, String country, double price, String type, String size, String imageUrl) {
//        this.owner = owner;
//        this.location = location;
//        this.country = country;
//        this.price = price;
//        this.type = type;
//        this.size = size;
//        this.imageUrl = imageUrl;
//  }
//
//    public String getOwner() { return owner; }
//    public String getLocation() { return location; }
//    public String getCountry() { return country; }
//    public double getPrice() { return price; }
//    public String getType() { return type; }
//    public String getSize() { return size; }
//    public String getImageUrl() { return imageUrl; }
//}




package com.example.realestateapp.PropertyManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Property {
    private String owner;
    private String location;
    private String country;
    private double price;
    private String type;
    private String size;
    private String imageUrl;

//
//    public Property() {}
//
//    public Property(String owner, String location, String country, double price, String type, String size, String imageUrl) {
//        this.owner = owner;
//        this.location = location;
//        this.country = country;
//        this.price = price;
//        this.type = type;
//        this.size = size;
//        this.imageUrl = imageUrl;
//    }
//
//    // Getters and Setters
//    public String getOwner() { return owner; }
//    public void setOwner(String owner) { this.owner = owner; }
//
//    public String getLocation() { return location; }
//    public void setLocation(String location) { this.location = location; }
//
//    public String getCountry() { return country; }
//    public void setCountry(String country) { this.country = country; }
//
//    public double getPrice() { return price; }
//    public void setPrice(double price) { this.price = price; }
//
//    public String getType() { return type; }
//    public void setType(String type) { this.type = type; }
//
//    public String getSize() { return size; }
//    public void setSize(String size) { this.size = size; }
//
//    public String getImageUrl() { return imageUrl; }
//    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
