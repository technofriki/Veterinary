package com.mokah.veterinary.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage = switch (authException) {
            case BadCredentialsException b -> "Credenciales inválidas";
            case DisabledException d -> "Cuenta deshabilitada";
            case LockedException l -> "Cuenta bloqueada";
            case AccountExpiredException a -> "Cuenta expirada";
            case CredentialsExpiredException c -> "Credenciales expiradas";
            case InsufficientAuthenticationException i -> "Autenticación insuficiente";
            case AuthenticationServiceException as -> "Error en el servicio de autenticación";
            default -> "Error de autenticación: " + authException.getMessage();
        };

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("error", errorMessage);
        responseData.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        responseData.put("path", request.getRequestURI());

        response.getWriter().write(mapper.writeValueAsString(responseData));
        response.getWriter().flush();
    }
}
