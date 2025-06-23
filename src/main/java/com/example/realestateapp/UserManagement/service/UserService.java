package com.example.realestateapp.UserManagement.service;

import com.example.realestateapp.UserManagement.model.User;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private final String filePath = "data/users.txt";

    public boolean emailExists(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[2].equalsIgnoreCase(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean userExists(String username) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equalsIgnoreCase(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean gmailExists(String gmail) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[2].equalsIgnoreCase(gmail)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void saveUser(User user) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getGmail());
            writer.newLine();
        }
    }

    public boolean validateUser(String username, String password) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    public User findUserByUsername(String username) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(username)) {
                    return new User(parts[0], parts[1], parts[2]);
                }
            }
        }
        return null;
    }

    public void updateUser(String oldUsername, User updatedUser) throws IOException {
        List<User> users = loadUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users) {
                if (user.getUsername().equals(oldUsername)) {
                    writer.write(updatedUser.getUsername() + "," + updatedUser.getPassword() + "," + updatedUser.getGmail());
                } else {
                    writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getGmail());
                }
                writer.newLine();
            }
        }
    }

    public void updatePasswordByEmail(String email, String newPassword) throws IOException {
        List<User> users = loadUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users) {
                if (user.getGmail().equalsIgnoreCase(email)) {
                    writer.write(user.getUsername() + "," + newPassword + "," + user.getGmail());
                } else {
                    writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getGmail());
                }
                writer.newLine();
            }
        }
    }

    public void deleteUser(String username) throws IOException {
        List<User> users = loadUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users) {
                if (!user.getUsername().equals(username)) {
                    writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getGmail());
                    writer.newLine();
                }
            }
        }
    }

    public List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        }
        return users;
    }

    public int getUserCount() throws IOException {
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

    // ✅ NEW: Get the latest 5 registered users (bottom of file)
    public List<User> getLatestUsers() throws IOException {
        List<User> users = loadUsers();
        int fromIndex = Math.max(0, users.size() - 5);
        return users.subList(fromIndex, users.size());
    }
}
