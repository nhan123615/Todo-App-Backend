package com.todo.restfulapi.services;

import com.todo.restfulapi.entities.CustomUserDetails;
import com.todo.restfulapi.entities.LoginRequest;
import com.todo.restfulapi.entities.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {
    Map<String, String> login(LoginRequest loginRequest);
    Map<String, String> getRefreshToken(String token);
    CustomUserDetails processUserOAuth(String username, String name);
    User register(LoginRequest registerRequest);
    void forgotPassword(String username,String redirectUrl);
    void updatePassword(String password, HttpServletRequest request);
}
