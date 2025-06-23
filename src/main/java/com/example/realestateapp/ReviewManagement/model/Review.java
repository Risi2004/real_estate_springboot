package com.example.realestateapp.ReviewManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Review {
    private String name;
    private String date;
    private String rating;
    private String comment;

//    public Review() {}
//
//    public Review(String name, String date, String rating, String comment) {
//        this.name = name;
//        this.date = date;
//        this.rating = rating;
//        this.comment = comment;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public String getRating() {
//        return rating;
//    }
//
//    public String getComment() {
//        return comment;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public void setRating(String rating) {
//        this.rating = rating;
//    }
//
//    public void setComment(String comment) {
//        this.comment = comment;
//    }
}
