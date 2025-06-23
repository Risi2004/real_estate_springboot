package com.example.realestateapp.PropertyManagement.service;

import com.example.realestateapp.MailManagement.service.MailService;
import com.example.realestateapp.PropertyManagement.BinarySearchTree.MyPropertyList;
import com.example.realestateapp.PropertyManagement.BinarySearchTree.PropertyBST;
import com.example.realestateapp.PropertyManagement.model.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class PropertyService {
    private final String filePath = "data/properties.txt";

    @Autowired
    private MailService mailService;

//    public void saveProperty(Property property) throws IOException {
//        File file = new File(filePath);
//        file.getParentFile().mkdirs();
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
//            writer.write(property.getOwner() + "," + property.getLocation() + "," + property.getCountry() + "," +
//                    property.getPrice() + "," + property.getType() + "," + property.getSize() + "," + property.getImageUrl());
//            writer.newLine();
//        }
//
//        String email = mailService.getEmailByUsername(property.getOwner());
//        if (email != null) {
//            String body = "Your property at " + property.getLocation() + ", " + property.getCountry() +
//                    " has been successfully ADDED.\n\nDetails:\nPrice: " + property.getPrice() +
//                    "\nType: " + property.getType() + "\nSize: " + property.getSize();
//            mailService.sendGeneralNotificationEmail(email, "Property Added – Real Estate App", body);
//        }
//    }




    public void saveProperty(Property property) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        // Step 1: Load existing properties
        MyPropertyList all = getAllProperties();
        all.add(property);

        // Step 2: Sort using BST
        PropertyBST tree = new PropertyBST();
        for (int i = 0; i < all.size(); i++) {
            tree.insert(all.get(i));
        }
        MyPropertyList sorted = tree.inOrderTraversal();

        // Step 3: Rewrite the file with sorted properties
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (int i = 0; i < sorted.size(); i++) {
                Property p = sorted.get(i);
                writer.write(p.getOwner() + "," + p.getLocation() + "," + p.getCountry() + "," +
                        p.getPrice() + "," + p.getType() + "," + p.getSize() + "," + p.getImageUrl());
                writer.newLine();
            }
        }

        // Step 4: Send email
        String email = mailService.getEmailByUsername(property.getOwner());
        if (email != null) {
            String body = "Your property at " + property.getLocation() + ", " + property.getCountry() +
                    " has been successfully ADDED.\n\nDetails:\nPrice: " + property.getPrice() +
                    "\nType: " + property.getType() + "\nSize: " + property.getSize();
            mailService.sendGeneralNotificationEmail(email, "Property Added – Real Estate App", body);
        }
    }



    public PropertyBST buildPropertyTree() {
        PropertyBST tree = new PropertyBST();
        File file = new File(filePath);

        if (!file.exists()) return tree;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 7);
                if (parts.length == 7) {
                    Property p = new Property(parts[0], parts[1], parts[2],
                            Double.parseDouble(parts[3]), parts[4], parts[5], parts[6]);
                    tree.insert(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tree;
    }

    public MyPropertyList getAllProperties() {
        return buildPropertyTree().inOrderTraversal();
    }

    public MyPropertyList getLatestProperties() {
        MyPropertyList all = getAllProperties();
        MyPropertyList latest = new MyPropertyList();

        int start = Math.max(0, all.size() - 5);
        for (int i = start; i < all.size(); i++) {
            latest.add(all.get(i));
        }
        return latest;
    }

    public MyPropertyList searchByLocation(String location) {
        return buildPropertyTree().searchByLocation(location);
    }

    public void quickSortByPrice(MyPropertyList list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSortByPrice(list, low, pi - 1);
            quickSortByPrice(list, pi + 1, high);
        }
    }

    private int partition(MyPropertyList list, int low, int high) {
        double pivot = list.get(high).getPrice();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).getPrice() < pivot) {
                i++;
                swap(list, i, j);
            }
        }

        swap(list, i + 1, high);
        return i + 1;
    }

    private void swap(MyPropertyList list, int i, int j) {
        Property temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    public void updateProperty(String owner, String originalLocation, Property updatedProperty) throws IOException {
        File file = new File(filePath);
        StringBuilder result = new StringBuilder();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 7);
                if (parts.length == 7 && parts[0].equals(owner) && parts[1].equals(originalLocation)) {
                    updated = true;
                    result.append(updatedProperty.getOwner()).append(",")
                            .append(updatedProperty.getLocation()).append(",")
                            .append(updatedProperty.getCountry()).append(",")
                            .append(updatedProperty.getPrice()).append(",")
                            .append(updatedProperty.getType()).append(",")
                            .append(updatedProperty.getSize()).append(",")
                            .append(updatedProperty.getImageUrl()).append("\n");
                } else {
                    result.append(line).append("\n");
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(result.toString());
        }

        if (updated) {
            String email = mailService.getEmailByUsername(updatedProperty.getOwner());
            if (email != null) {
                String body = "Your property at " + updatedProperty.getLocation() + ", " + updatedProperty.getCountry() +
                        " has been successfully UPDATED.\n\nNew Details:\nPrice: " + updatedProperty.getPrice() +
                        "\nType: " + updatedProperty.getType() + "\nSize: " + updatedProperty.getSize();
                mailService.sendGeneralNotificationEmail(email, "Property Updated – Real Estate App", body);
            }
        }
    }

    public void deleteProperty(String owner, String location) throws IOException {
        File file = new File(filePath);
        StringBuilder result = new StringBuilder();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 7);
                if (parts.length == 7 && parts[0].equals(owner) && parts[1].equals(location)) {
                    found = true;
                    continue;
                }
                result.append(line).append("\n");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(result.toString());
        }

        if (found) {
            String email = mailService.getEmailByUsername(owner);
            if (email != null) {
                String body = "Your property at " + location + " has been successfully DELETED from the Real Estate App.";
                mailService.sendGeneralNotificationEmail(email, "Property Deleted – Real Estate App", body);
            }
        }
    }

    public int getPropertyCount() throws IOException {
        int count = 0;
        File file = new File(filePath);
        if (!file.exists()) return 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) count++;
            }
        }
        return count;
    }

    public double calculateTotalRevenue() {
        double totalRevenue = 0;
        File file = new File(filePath);
        if (!file.exists()) return 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 7);
                if (parts.length >= 4) {
                    try {
                        double price = Double.parseDouble(parts[3]);
                        totalRevenue += price * 0.10;
                    } catch (NumberFormatException e) {
                        System.err.println("ERROR parsing price: " + parts[3]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalRevenue;
    }
}
