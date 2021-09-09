package com.todo.restfulapi;

import com.todo.restfulapi.entities.User;
import com.todo.restfulapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class RestfulapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestfulapiApplication.class, args);
    }

}
