package com.todo.restfulapi.security;


import com.todo.restfulapi.consts.AuthConsts;
import com.todo.restfulapi.entities.CustomUserDetails;
import com.todo.restfulapi.services.UserServiceImpl;
import com.todo.restfulapi.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserServiceImpl userServiceImpl;
    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Map<String, String> tokens = null;
        Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            CustomUserDetails userDetails = userServiceImpl.processUserOAuth(String.valueOf(oAuth2User.getAttributes().get("email")), String.valueOf(oAuth2User.getAttributes().get("name")));
            tokens = tokenProvider.generateToken(userDetails);
        }
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam(AuthConsts.ACCESS_TOKEN, tokens.get(AuthConsts.ACCESS_TOKEN))
                .queryParam(AuthConsts.REFRESH_TOKEN, tokens.get(AuthConsts.REFRESH_TOKEN))
                .queryParam("tokenType", AuthConsts.BEARER)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

}
