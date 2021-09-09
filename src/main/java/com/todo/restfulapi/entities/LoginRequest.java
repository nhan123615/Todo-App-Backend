package com.todo.restfulapi.entities;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String name;
    private String password;
}
