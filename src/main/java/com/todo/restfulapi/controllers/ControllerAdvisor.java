package com.todo.restfulapi.controllers;

import com.todo.restfulapi.entities.CustomUserDetails;
import com.todo.restfulapi.entities.User;
import com.todo.restfulapi.exceptions.ApiRequestException;
import com.todo.restfulapi.exceptions.CustomException;
import com.todo.restfulapi.services.UserService;
import com.todo.restfulapi.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e){
        CustomException exception = new CustomException(
                ZonedDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN,
                e.getMessage()
        );

        return new ResponseEntity<>(exception,HttpStatus.FORBIDDEN);
    }

}
