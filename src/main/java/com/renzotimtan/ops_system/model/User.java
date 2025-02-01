package com.renzotimtan.ops_system.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String username;

    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
}
