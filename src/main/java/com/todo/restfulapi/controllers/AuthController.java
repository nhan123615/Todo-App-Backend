package com.todo.restfulapi.controllers;

import com.todo.restfulapi.consts.AuthConsts;
import com.todo.restfulapi.entities.*;
import com.todo.restfulapi.services.UserService;
import com.todo.restfulapi.services.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/auth", produces = "application/json")
@CrossOrigin("*")
@Slf4j
public class AuthController {
    private UserService userServiceImpl;

    @Autowired
    public AuthController(UserService userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(new LoginResponse(userServiceImpl.login(loginRequest)));
    }


    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody LoginRequest registerRequest) {
        return ResponseEntity.created(null).body(userServiceImpl.register(registerRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request) {
        String token = request.getHeader(AuthConsts.AUTHORIZATION);
        return ResponseEntity.ok().body(new LoginResponse(userServiceImpl.getRefreshToken(token)));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String,Object> payload) {
        userServiceImpl.forgotPassword(payload.get("username").toString(),payload.get("redirectUrl").toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String,Object> payload,HttpServletRequest request) {
        userServiceImpl.updatePassword(payload.get("password").toString(),request);
        return ResponseEntity.ok().build();
    }

}
