package org.git.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.git.api.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(StringUtils.isNotBlank(request.getHeader(HttpHeaders.AUTHORIZATION))) {
            boolean valid = jwtService.isTokenValid(request.getHeader(HttpHeaders.AUTHORIZATION));
            if(valid) {
                filterChain.doFilter(request, response);
            } else {
                throw new RuntimeException("Invalid JWT token");
            }
        } else {
            throw new RuntimeException("JWT token required");
        }
    }

}
