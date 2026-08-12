package com.financialtracker.backend.Confiig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {
	ObjectMapper objectmapper=new ObjectMapper();
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		Map<String, Object> error=new HashMap<String, Object>();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		error.put("status", 401);
		error.put("msg", "Please enter the credentials");
		response.getWriter().write(objectmapper.writeValueAsString(error));
		
	}

}
