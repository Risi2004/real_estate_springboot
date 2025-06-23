package com.example.realestateapp.PropertyManagement.controller;

import com.example.realestateapp.PropertyManagement.model.Property;
import com.example.realestateapp.PropertyManagement.service.PropertyService;
import com.example.realestateapp.PropertyManagement.BinarySearchTree.MyPropertyList;
import com.example.realestateapp.MailManagement.service.MailService;
import com.example.realestateapp.BookingManagement.service.ContactRequestService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;

@Controller
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private MailService mailService;

    @Autowired
    private ContactRequestService contactService;

    @GetMapping("/sales")
    public String showSalesSortedByPrice(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        // ✅ Sort a copy so BST-based order isn't altered
        MyPropertyList allProperties = propertyService.getAllProperties();
        MyPropertyList sortedList = allProperties.copy();
        propertyService.quickSortByPrice(sortedList, 0, sortedList.size() - 1);

        model.addAttribute("properties", sortedList.toArray());
        return "sales";
    }

    @GetMapping("/sales-location")
    public String showSalesSortedByLocation(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        model.addAttribute("properties", propertyService.buildPropertyTree().inOrderTraversal().toArray());
        return "sales";
    }

    @GetMapping("/search-location")
    public String searchByLocation(@RequestParam String query, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        MyPropertyList all = propertyService.getAllProperties();
        MyPropertyList matched = new MyPropertyList();

        String input = query.trim().toLowerCase();

        for (int i = 0; i < all.size(); i++) {
            Property p = all.get(i);
            String location = p.getLocation().toLowerCase();
            if (levenshtein(location, input) <= 2) {
                matched.add(p);
            }
        }

        model.addAttribute("properties", matched.toArray());
        return "sales";
    }

    @GetMapping("/add-property")
    public String addPropertyPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        model.addAttribute("username", username);
        return "add-property";
    }

    @PostMapping("/add-property")
    public String addProperty(@RequestParam String location,
                              @RequestParam String country,
                              @RequestParam double price,
                              @RequestParam String type,
                              @RequestParam String size,
                              @RequestParam String imageUrl,
                              HttpSession session) throws IOException {

        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        Property p = new Property(username, location, country, price, type, size, imageUrl);
        session.setAttribute("pendingProperty", p);

        return "redirect:/card-details?price=" + price;
    }

    @GetMapping("/card-details")
    public String showCardForm(HttpSession session, Model model, @RequestParam double price) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        model.addAttribute("price", price);

        File file = new File("data/carddetails.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(username + ",")) {
                        String[] parts = line.split(",");
                        if (parts.length == 4) {
                            model.addAttribute("cardNumber", parts[1]);
                            model.addAttribute("expiry", parts[2]);
                            model.addAttribute("cvv", parts[3]);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "card-details";
    }

    @PostMapping("/card-details")
    public String processCardDetails(@RequestParam String cardNumber,
                                     @RequestParam String expiry,
                                     @RequestParam String cvv,
                                     @RequestParam double price,
                                     HttpSession session) throws IOException {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        File file = new File("data/carddetails.txt");
        file.getParentFile().mkdirs();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith(username + ",")) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        }

        writer.write(username + "," + cardNumber + "," + expiry + "," + cvv);
        writer.newLine();
        writer.close();

        return "redirect:/payment-gateway?price=" + price;
    }

    @GetMapping("/payment-gateway")
    public String showPaymentGateway(@RequestParam double price, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        model.addAttribute("amount", price * 0.10);
        return "payment-gateway";
    }

    @PostMapping("/payment-success")
    public String completePayment(HttpSession session) throws IOException {
        Property p = (Property) session.getAttribute("pendingProperty");
        if (p != null) {
            propertyService.saveProperty(p);
            session.removeAttribute("pendingProperty");

            double amount = p.getPrice() * 0.10;
            String invoiceId = "INV-" + System.currentTimeMillis();
            session.setAttribute("invoiceId", invoiceId);

            String receiptText = "Invoice: " + invoiceId + "\n"
                    + "Username: " + p.getOwner() + "\n"
                    + "Location: " + p.getLocation() + "\n"
                    + "Country: " + p.getCountry() + "\n"
                    + "Price: $" + p.getPrice() + "\n"
                    + "Paid: $" + amount + "\n"
                    + "Status: Success\n";

            File txtFile = new File("data/receipt.txt");
            txtFile.getParentFile().mkdirs();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile, true))) {
                writer.write(receiptText + "\n");
            }

            File pdfDir = new File("data/receipts");
            pdfDir.mkdirs();
            File pdfFile = new File(pdfDir, invoiceId + ".pdf");

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                document.add(new Paragraph("Real Estate App - Payment Receipt"));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(receiptText));
                document.close();
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            session.setAttribute("lastReceipt", receiptText);
            mailService.sendPaymentReceipt(p.getOwner(), receiptText);

            return "redirect:/payment-receipt";
        }
        return "redirect:/sales";
    }

    @GetMapping("/payment-receipt")
    public String showReceipt(HttpSession session, Model model) {
        model.addAttribute("receipt", session.getAttribute("lastReceipt"));
        model.addAttribute("invoiceId", session.getAttribute("invoiceId"));
        return "payment-receipt";
    }

    @GetMapping("/download-receipt")
    public void downloadPdf(@RequestParam String invoice, HttpServletResponse response) throws IOException {
        File pdf = new File("data/receipts/" + invoice + ".pdf");
        if (pdf.exists()) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + invoice + ".pdf");
            Files.copy(pdf.toPath(), response.getOutputStream());
            response.flushBuffer();
        }
    }

    @PostMapping("/delete-property")
    public String deleteProperty(@RequestParam String location, HttpSession session) throws IOException {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";
        propertyService.deleteProperty(username, location);
        return "redirect:/sales";
    }

    @GetMapping("/edit-property")
    public String showEditForm(@RequestParam String location, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        MyPropertyList allProps = propertyService.getAllProperties();
        for (int i = 0; i < allProps.size(); i++) {
            Property p = allProps.get(i);
            if (p.getOwner().equals(username) && p.getLocation().equals(location)) {
                model.addAttribute("property", p);
                model.addAttribute("username", username);
                return "edit-property";
            }
        }
        return "redirect:/sales";
    }

    @PostMapping("/edit-property")
    public String updateProperty(@RequestParam String originalLocation,
                                 @RequestParam String location,
                                 @RequestParam String country,
                                 @RequestParam double price,
                                 @RequestParam String type,
                                 @RequestParam String size,
                                 @RequestParam String imageUrl,
                                 HttpSession session) throws IOException {
        String username = (String) session.getAttribute("username");
        if (username == null) return "redirect:/login";

        Property updated = new Property(username, location, country, price, type, size, imageUrl);
        propertyService.updateProperty(username, originalLocation, updated);
        return "redirect:/sales";
    }

    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0)
                    dp[i][j] = j;
                else if (j == 0)
                    dp[i][j] = i;
                else if (a.charAt(i - 1) == b.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
        return dp[a.length()][b.length()];
    }
}
