package com.renzotimtan.ops_system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Data
@Document(collection = "orders")
public class Order {

    @Id
    @Schema(hidden = true)
    private String id;

    @DBRef
    private User user;

    private Map<String, Integer> productQuantities;
    private double totalAmount;
    private String orderTime;
}
