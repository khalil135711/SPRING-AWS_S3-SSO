package largescaleit_demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import largescaleit_demo.models.Order;
import largescaleit_demo.repositories.S3ProRepository;



@Controller
public class OrderController {
    @Autowired
    private S3ProRepository s3ProRepository;

    @GetMapping("/orders-view")
    public String viewOrders(Model model) {
        try {
            List<Order> orders = s3ProRepository.getAllOrders();
            model.addAttribute("orders", orders);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            model.addAttribute("error", "Unable to fetch orders"); // Optionally add error message to model
        }
        return "orders-view";
    }
    @PostMapping("/end-order")
    public String endOrder(@RequestParam("orderId") String orderIdStr) {
        System.out.println("Received request to end order with ID: " + orderIdStr);

        UUID orderId = null;
        try {
            if (orderIdStr != null && !orderIdStr.isEmpty()) {
                orderId = UUID.fromString(orderIdStr);
                System.out.println("Parsed UUID: " + orderId);
            } else {
                System.err.println("Order ID is null or empty");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid UUID format: " + orderIdStr);
        }

        if (orderId != null) {
            try {
                s3ProRepository.deleteOrderFromS3(orderId);
                System.out.println("Successfully requested deletion for order ID: " + orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Order ID is null or invalid");
        }

        return "redirect:/orders-view";
    }
}
