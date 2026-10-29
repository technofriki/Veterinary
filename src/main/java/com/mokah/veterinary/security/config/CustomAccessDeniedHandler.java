package com.mokah.veterinary.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("error", "Acceso denegado: permisos insuficientes");
        responseData.put("status", HttpServletResponse.SC_FORBIDDEN);
        responseData.put("path", request.getRequestURI());

        response.getWriter().write(mapper.writeValueAsString(responseData));
        response.getWriter().flush();
    }
}
