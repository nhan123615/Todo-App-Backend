package com.todo.restfulapi.entities;

import com.todo.restfulapi.consts.AuthConsts;
import lombok.Data;

import java.util.Map;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = AuthConsts.BEARER;

    public LoginResponse(Map<String,String> tokens) {
        this.accessToken = tokens.get(AuthConsts.ACCESS_TOKEN);
        this.refreshToken = tokens.get(AuthConsts.REFRESH_TOKEN);
    }
}
