package com.todo.restfulapi.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class CustomException {
    private final ZonedDateTime timestamp;
    private final int status;
    private final HttpStatus error;
    private final String message;
}
