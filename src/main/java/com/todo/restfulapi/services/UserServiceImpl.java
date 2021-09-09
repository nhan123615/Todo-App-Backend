package com.todo.restfulapi.services;

import com.todo.restfulapi.security.JwtTokenProvider;
import com.todo.restfulapi.consts.AuthConsts;
import com.todo.restfulapi.entities.CustomUserDetails;
import com.todo.restfulapi.entities.LoginRequest;
import com.todo.restfulapi.entities.User;
import com.todo.restfulapi.exceptions.ApiRequestException;
import com.todo.restfulapi.repositories.UserRepository;
import com.todo.restfulapi.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Map;


@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;
    private EmailUtils emailUtils;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, EmailUtils emailUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.emailUtils = emailUtils;
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user);
    }

    @Override
    public Map<String, String> login(LoginRequest loginRequest) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            if (user.getUser().getRole().equals(AuthConsts.ROLE_ADMIN)) {
                throw new ApiRequestException("Admin role is developing");
            }
            return tokenProvider.generateToken(user);
    }

    @Override
    public Map<String, String> getRefreshToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(AuthConsts.BEARER + " ")) {
            String bearerToken = token.substring((AuthConsts.BEARER + " ").length());

            if (StringUtils.hasText(bearerToken) && tokenProvider.validateToken(bearerToken)) {
                String username = tokenProvider.getUsernameFromJWT(bearerToken);
                UserDetails user = loadUserByUsername(username);
                if (user != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return tokenProvider.generateToken((CustomUserDetails) user);
                }
            }
        }
        throw new ApiRequestException("Token invalid!");
    }

    @Override
    public CustomUserDetails processUserOAuth(String username, String name) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new CustomUserDetails(userRepository.save(new User(
                    username,
                    null,
                    AuthConsts.ROLE_USER,
                    true,
                    name
            )));

        }
        return new CustomUserDetails(user);
    }

    @Override
    public User register(LoginRequest registerRequest) {
        User register = userRepository.findByUsername(registerRequest.getUsername());
        if (register != null) {
            throw new ApiRequestException("Email Address already exist!");
        }
        return userRepository.save(new User(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                AuthConsts.ROLE_USER,
                true,
                register.getName()
        ));
    }

    @Override
    public void forgotPassword(String username, String redirectUrl) {
        User forgotUser = userRepository.findByUsername(username);
        if (forgotUser != null && forgotUser.getPassword() != null) {
            String content = "<h1>The link to reset new password will expire in 10 minutes</h1><br>";
            content += redirectUrl + "?token=" + tokenProvider.generateResetPasswordToken(forgotUser) + "&tokenType=" + AuthConsts.BEARER;
            if (!emailUtils.send(username, "Forgot Password", content)) {
                throw new ApiRequestException("Cannot Send with this Email Address");
            }
        } else {
            throw new ApiRequestException("Email Address is not exist !");
        }
    }

    @Override
    public void updatePassword(String password, HttpServletRequest request) {
        String token = tokenProvider.getJwtFromRequest(request);
        if (tokenProvider.validateToken(token)) {
            User updatePasswordUser = userRepository.findByUsername(tokenProvider.getUsernameFromJWT(token));
            updatePasswordUser.setPassword(passwordEncoder.encode(password));
            userRepository.save(updatePasswordUser);
        }
    }

}
