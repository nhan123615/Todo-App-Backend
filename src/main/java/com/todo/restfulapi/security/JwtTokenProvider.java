package com.todo.restfulapi.security;

import com.todo.restfulapi.consts.AuthConsts;
import com.todo.restfulapi.entities.CustomUserDetails;
import com.todo.restfulapi.entities.User;
import com.todo.restfulapi.exceptions.ApiRequestException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String JWT_SECRET;
    @Value("${jwt.expiration.access-token}")
    private long JWT_EXPIRATION;
    @Value("${jwt.expiration.refresh-token}")
    private long JWT_EXPIRATION_REFRESH;
    @Value("${jwt.expiration.reset-token}")
    private long JWT_EXPIRATION_RESET;

    public String generateResetPasswordToken(User userDetails) {
        Date now = new Date();
        Date expiryDateReset = new Date(now.getTime() + JWT_EXPIRATION_RESET * 60 * 1000);
        String refresh_token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDateReset)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
        return refresh_token;
    }


    public Map<String, String> generateToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION * 60 * 1000);
        Date expiryDateRefresh = new Date(now.getTime() + JWT_EXPIRATION_REFRESH * 60 * 1000);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()).get(0));
        claims.put("name", userDetails.getUser().getName());

        String access_token = Jwts.builder()
                .setSubject(userDetails.getUser().getUsername())
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();

        String refresh_token = Jwts.builder()
                .setSubject(userDetails.getUser().getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDateRefresh)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();

        Map<String, String> tokens = new HashMap<>();
        tokens.put(AuthConsts.ACCESS_TOKEN, access_token);
        tokens.put(AuthConsts.REFRESH_TOKEN, refresh_token);
        return tokens;
    }


    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw new ApiRequestException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw new ApiRequestException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw new ApiRequestException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw new ApiRequestException("JWT claims string is empty.");
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthConsts.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AuthConsts.BEARER+" ")) {
            return bearerToken.substring((AuthConsts.BEARER+" ").length());
        }
        throw new ApiRequestException("Token invalid!");
    }
}
