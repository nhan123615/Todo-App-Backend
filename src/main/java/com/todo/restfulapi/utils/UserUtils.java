package com.todo.restfulapi.utils;

import com.todo.restfulapi.entities.CustomUserDetails;
import com.todo.restfulapi.entities.User;
import com.todo.restfulapi.services.UserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {
    public static User getUser(Authentication authentication, UserServiceImpl userService){
        User model = null;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof CustomUserDetails){
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                model = userDetails.getUser();
            }
            if (authentication.getPrincipal() instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                model = ((CustomUserDetails) userService.loadUserByUsername(String.valueOf(oAuth2User.getAttributes().get("email")))).getUser();
            }
        }
        return model;
    }
}
