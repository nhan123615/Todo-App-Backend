package com.todo.restfulapi.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "user")
@Data // lombok
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    private String name;
    private String password;
    private String role;
    private boolean enabled;

    public User(String username, String password, String role, boolean enabled,String name) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }
}
