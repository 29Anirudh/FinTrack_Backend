package com.financialtracker.backend.Filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.financialtracker.backend.Models.DL.ServicesImpl.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends OncePerRequestFilter{
	private final JWTService jwtservice;
	private final UserDetailsService usersdetailsservice;
	public JWTAuthenticationFilter(UserDetailsService usersdetailservice,JWTService jwtservice) {
		this.usersdetailsservice=usersdetailservice;
		this.jwtservice=jwtservice;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		 String token=null;
		 String username=null;
		 String authheader=request.getHeader("Authorization");
		if(authheader!=null && authheader.startsWith("Bearer ")) {
			token=authheader.substring(7);
			username=jwtservice.extractSubjectFromToken(token);
		}
		if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
			UserDetails userdetails=usersdetailsservice.loadUserByUsername(username);
			if(jwtservice.isValidToken(token)) {
				UsernamePasswordAuthenticationToken authtoken=new UsernamePasswordAuthenticationToken(userdetails,null, userdetails.getAuthorities());
				authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authtoken);
			}			
		}
		filterChain.doFilter(request, response);
	}
	
	
}
