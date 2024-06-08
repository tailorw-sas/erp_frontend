package com.kynsof.share.core.infrastructure.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.application.ProfileSecurity;
import com.kynsof.share.core.application.UserSecurity;
import com.kynsof.share.core.infrastructure.util.JwtParser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JwtWebSecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            if(requestTokenHeader != null) {
                UserSecurity userSecurity = parseToken(requestTokenHeader);
                var authentication = new UsernamePasswordAuthenticationToken(userSecurity, null, userSecurity.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.warn("Error parsing token", ex);
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private UserSecurity parseToken(String token) {
        ObjectMapper mapper = new ObjectMapper();
        Claims claims = JwtParser.parseToken(token);
        String subject = claims.getSubject();
        String role = claims.get("roles", String.class);
        List<ProfileSecurity> profiles = Collections.emptyList();
        List<?> profilesList = claims.get("profiles", List.class);
        if(profilesList != null) {
            profiles = mapper.convertValue(profilesList, new TypeReference<>() {});
        }
        Set<String> citizenAccessList = mapper.convertValue(claims.get("citizenAccess", List.class), new TypeReference<>() {});
        return new UserSecurity(subject, role, profiles, citizenAccessList);
    }
}
