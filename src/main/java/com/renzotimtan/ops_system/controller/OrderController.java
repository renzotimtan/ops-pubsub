package com.renzotimtan.ops_system.controller;

import com.renzotimtan.ops_system.model.Order;
import com.renzotimtan.ops_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam String userId,
                                             @RequestBody Map<String, Integer> productQuantities) {
        Order savedOrder = orderService.createOrder(userId, productQuantities);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable String id,
                                             @RequestBody Map<String, Integer> updatedProductQuantities) {
        Order updatedOrder = orderService.updateOrder(id, updatedProductQuantities);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable String id) {
        Order deletedOrder = orderService.deleteOrder(id);
        return ResponseEntity.ok(deletedOrder);
    }
}
