package com.example.chatp2p.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {

    @PostMapping("/api/auth/logout")
    public String logout(HttpServletRequest request, @AuthenticationPrincipal OidcUser oidcUser) throws ServletException {
        request.logout();
        return oidcUser.getIdToken().getTokenValue();
    }
} 