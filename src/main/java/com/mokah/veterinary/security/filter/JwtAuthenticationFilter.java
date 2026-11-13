package com.mokah.veterinary.security.filter;

import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.security.config.RestAuthenticationEntryPoint;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.security.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CredentialsRepository credentialsRepository;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {

            final String username =
                    jwtService.extractUsername(jwt);

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (username != null && authentication == null) {

                Credentials credentials = credentialsRepository.findByUsername(username).orElse(null);
                boolean active = credentials != null
                        && credentials.getUser() != null
                        && credentials.getUser().getUserState() == UserState.ACTIVE
                        && credentials.isEnabled()
                        && jwtService.isTokenValid(jwt, credentials);

                if (!active) {
                    // Public endpoints (login, refresh and documentation) must remain accessible
                    // even when a client sends a previously valid token for a disabled account.
                    String path = request.getServletPath();
                    boolean publicEndpoint = path.startsWith("/api/auth/")
                            || path.startsWith("/swagger-ui/")
                            || path.equals("/swagger-ui.html")
                            || path.startsWith("/v3/api-docs/")
                            || path.equals("/v3/api-docs");
                    if (!publicEndpoint) {
                        authenticationEntryPoint.commence(request, response,
                                new BadCredentialsException("Account inactive, credentials disabled or token invalid"));
                        return;
                    }
                } else {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, credentials.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (JwtException e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write(
                    String.format(
                            "{\"error\":\"Token JWT invalido o expirado\",\"status\":%d,\"path\":\"%s\"}",
                            HttpServletResponse.SC_UNAUTHORIZED,
                            request.getRequestURI()
                    )
            );

            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }
}
