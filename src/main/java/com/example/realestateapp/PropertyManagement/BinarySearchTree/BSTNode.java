package com.example.realestateapp.PropertyManagement.BinarySearchTree;

import com.example.realestateapp.PropertyManagement.model.Property;

public class BSTNode {
    public Property property;
    public BSTNode left;
    public BSTNode right;

    public BSTNode(Property property) {
        this.property = property;
        this.left = null;
        this.right = null;
    }
}
