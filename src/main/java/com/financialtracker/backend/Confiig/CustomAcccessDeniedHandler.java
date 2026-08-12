package com.financialtracker.backend.Confiig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

public class CustomAcccessDeniedHandler implements AccessDeniedHandler{
	ObjectMapper objectmapper=new ObjectMapper();
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.access.AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
		Map<String, Object> error=new HashMap<String, Object>();
		response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
		response.setContentType("application/json");
		error.put("status", 403);
		error.put("msg", "You are not allowed to access this page");
		response.getWriter().write(objectmapper.writeValueAsString(error));
		
	}

}
