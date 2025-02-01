package com.renzotimtan.ops_system.service;

import com.renzotimtan.ops_system.model.Order;
import com.renzotimtan.ops_system.model.Product;
import com.renzotimtan.ops_system.model.User;
import com.renzotimtan.ops_system.repository.OrderRepository;
import com.renzotimtan.ops_system.repository.ProductRepository;
import com.renzotimtan.ops_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.pubsub.topic:order}")
    private String orderTopic;

    public Order createOrder(String userId, Map<String, Integer> productQuantities) {

        // Fetch product details from DB
        List<String> productIds = List.copyOf(productQuantities.keySet());
        List<Product> products = productRepository.findAllById(productIds);

        if (products.isEmpty()) {
            throw new RuntimeException("No valid products found for IDs: " + productIds);
        }

        // Calculate total price
        double total = products.stream()
            .mapToDouble(product -> {
                int quantity = productQuantities.get(product.getId());
                return quantity * product.getPrice();
            })
            .sum();

        // Create order
        Order order = new Order();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        order.setUser(user);
        order.setProductQuantities(productQuantities);
        order.setTotalAmount(total);
        order.setOrderTime(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

         // Convert Order object to JSON and publish
         try {
            String json = objectMapper.writeValueAsString(order);
            pubSubTemplate.publish(orderTopic, json);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing order to JSON", e);
        }

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order updateOrder(String id, Map<String, Integer> updatedProductQuantities) {
        // Fetch existing order
        Order existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        List<String> productIds = List.copyOf(updatedProductQuantities.keySet());

        // Fetch products from the database
        List<Product> products = productRepository.findAllById(productIds);

        if (products.isEmpty()) {
            throw new RuntimeException("No valid products found for IDs: " + productIds);
        }

        // Calculate total price
        double newTotal = products.stream()
            .mapToDouble(product -> {
                int quantity = updatedProductQuantities.get(product.getId());
                return quantity * product.getPrice();
            })
            .sum();

        // Set new values
        existingOrder.setProductQuantities(updatedProductQuantities);
        existingOrder.setTotalAmount(newTotal);

        // Save and return the updated order
        return orderRepository.save(existingOrder);
    }


    public Order deleteOrder(String id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        orderRepository.deleteById(id);
        return order;
    }
}
