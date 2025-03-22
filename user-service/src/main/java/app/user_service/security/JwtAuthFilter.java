package app.user_service.security;

import app.user_service.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    public JwtAuthFilter(JwtUtil jwtUtil, TokenBlacklistService blacklistService) {
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (blacklistService.isTokenBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has been blacklisted.");
            return;
        }

        try {
            Claims claims = jwtUtil.parseToken(token);

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(authority));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
