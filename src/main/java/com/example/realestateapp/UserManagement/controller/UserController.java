package com.example.realestateapp.UserManagement.controller;

import com.example.realestateapp.PropertyManagement.service.PropertyService;
import com.example.realestateapp.ReviewManagement.model.Review;
import com.example.realestateapp.ReviewManagement.service.ReviewService;
import com.example.realestateapp.UserManagement.model.User;
import com.example.realestateapp.UserManagement.service.UserService;
import com.example.realestateapp.MailManagement.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MailService mailService;

    private final Map<String, String> resetTokens = new HashMap<>();

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String gmail,
                         Model model) {
        try {
            if (userService.userExists(username)) {
                model.addAttribute("message", "Username already exists. Please choose another.");
            } else if (userService.gmailExists(gmail)) {
                model.addAttribute("message", "Email already registered. Please use another.");
            } else {
                userService.saveUser(new User(username, password, gmail));
                mailService.sendSignupEmail(gmail, username);
                return "redirect:/login";
            }
        } catch (IOException e) {
            model.addAttribute("message", "Signup failed.");
        }
        return "signup";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            if ("admin".equals(username) && "admin123".equals(password)) {
                session.setAttribute("username", username);
                return "redirect:/admin-dashboard";
            }

            if (userService.validateUser(username, password)) {
                session.setAttribute("username", username);
                String email = mailService.getEmailByUsername(username);
                if (email != null) {
                    mailService.sendLoginEmail(email, username);
                }
                return "redirect:/home";
            } else {
                model.addAttribute("message", "Invalid username or password.");
            }
        } catch (IOException e) {
            model.addAttribute("message", "Login error.");
        }
        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendResetEmail(@RequestParam String email, Model model) {
        try {
            if (userService.gmailExists(email)) {
                String token = UUID.randomUUID().toString();
                resetTokens.put(token, email);
                mailService.sendResetLink(email, token);
                model.addAttribute("message", "Password reset email sent.");
            } else {
                model.addAttribute("message", "Email not found.");
            }
        } catch (IOException e) {
            model.addAttribute("message", "Error checking email. Please try again.");
            e.printStackTrace();
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        if (!resetTokens.containsKey(token)) {
            model.addAttribute("error", "Invalid or expired token.");
            return "error";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processReset(@RequestParam String token,
                               @RequestParam String newPassword,
                               Model model) {
        String email = resetTokens.get(token);
        if (email != null) {
            try {
                userService.updatePasswordByEmail(email, newPassword);
                resetTokens.remove(token);
                mailService.sendGeneralNotificationEmail(
                        email,
                        "Your Password Has Been Changed",
                        "This is a confirmation that your password was successfully changed on Real Estate App. If you did not perform this action, please contact support immediately."
                );
                model.addAttribute("message", "Password updated successfully.");
                return "login";
            } catch (IOException e) {
                model.addAttribute("error", "Failed to update password.");
                e.printStackTrace();
                return "error";
            }
        } else {
            model.addAttribute("error", "Invalid token.");
            return "error";
        }
    }

    @GetMapping("/admin-home")
    public String adminHome(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (!"admin".equals(username)) return "redirect:/login";

        model.addAttribute("username", username);

        try {
            int userCount = userService.getUserCount();
            int propertyCount = propertyService.getPropertyCount();
            model.addAttribute("userCount", userCount);
            model.addAttribute("propertyCount", propertyCount);
            var allUsers = userService.loadUsers();
            var lastFiveUsers = allUsers.stream().skip(Math.max(0, allUsers.size() - 5)).collect(Collectors.toList());
            var allProperties = propertyService.getAllProperties();
//            var lastFiveProperties = allProperties.stream().skip(Math.max(0, allProperties.size() - 5)).collect(Collectors.toList());
            model.addAttribute("userList", lastFiveUsers);
//            model.addAttribute("propertyList", lastFiveProperties);
        } catch (IOException e) {
            model.addAttribute("userCount", 0);
            model.addAttribute("propertyCount", 0);
            model.addAttribute("userList", new ArrayList<>());
            model.addAttribute("propertyList", new ArrayList<>());
        }
        return "admin-dashboard";
    }

    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        model.addAttribute("username", username);
        try {
            List<Review> reviews = reviewService.getAllReviews(); // ✅ Uses getAllReviews()
            model.addAttribute("reviews", reviews);
        } catch (Exception e) {
            model.addAttribute("reviews", List.of());
        }
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/edit")
    public String showEditPage(HttpSession session, Model model) throws IOException {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("/edit")
    public String updateUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String gmail,
                             HttpSession session) throws IOException {
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername == null) return "redirect:/login";
        userService.updateUser(currentUsername, new User(username, password, gmail));
        session.setAttribute("username", username);
        String email = mailService.getEmailByUsername(username);
        if (email != null) mailService.sendUserUpdateEmail(email, username);
        return "redirect:/home";
    }

    @GetMapping("/delete")
    public String deleteAccount(HttpSession session) throws IOException {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            String email = mailService.getEmailByUsername(username);
            userService.deleteUser(username);
            session.invalidate();
            if (email != null) {
                mailService.sendGeneralNotificationEmail(
                        email,
                        "Your Real Estate Account Was Deleted",
                        "Hi " + username + ",\n\nYour account on Real Estate App has been successfully deleted. If this was not done by you, please contact support immediately.\n\nRegards,\nReal Estate App Team"
                );
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/admin_users")
    public String viewRegisteredUsers(Model model, HttpSession session) throws IOException {
        String username = (String) session.getAttribute("username");
        if (!"admin".equals(username)) return "redirect:/login";
        model.addAttribute("userList", userService.loadUsers());
        return "admin_users";
    }

    @PostMapping("/admin_users/delete")
    public String deleteUserFromAdmin(@RequestParam String username, HttpSession session) throws IOException {
        String admin = (String) session.getAttribute("username");
        if (!"admin".equals(admin)) return "redirect:/login";
        userService.deleteUser(username);
        return "redirect:/admin_users";
    }

    @GetMapping("/contact")
    public String showContactPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);
        return "contact";
    }
}
