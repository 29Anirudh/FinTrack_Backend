package com.financialtracker.backend.Confiig;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.financialtracker.backend.Filter.JWTAuthenticationFilter;
import com.financialtracker.backend.Models.BL.AccountBL;
import com.financialtracker.backend.Models.BL.TransactionBL;
import com.financialtracker.backend.Models.BL.UsersBL;
import com.financialtracker.backend.Models.DL.ServicesImpl.JWTService;
import com.financialtracker.backend.Models.DL.ServicesImpl.UsersDetailsService;

@Configuration
public class AppConfig {
	@Bean
	public PasswordEncoder passwordencoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UsersBL usersbl() {
		return new UsersBL();
	}
	@Bean
	public AccountBL accountbl() {
		return new AccountBL();
	}
	@Bean
	public TransactionBL transactionBL(){
		return new TransactionBL();
	}
	@Bean
	public AuthenticationEntryPointHandler authenticationEntryPointHandler() {
		return new AuthenticationEntryPointHandler();
	}
	@Bean
	public CustomAcccessDeniedHandler customAcccessDeniedHandler() {
		return new CustomAcccessDeniedHandler();
	}
	@Bean
	public JWTService jwtservice() {
		return new JWTService();
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.cors(cors -> {})
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/user/**").permitAll()
						.requestMatchers("/account/**").hasAnyRole("USER","ADMIN")
						.requestMatchers("/transactions/**").hasRole("USER")
						.anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(ex -> ex
	                    .authenticationEntryPoint(authenticationEntryPointHandler())
	                    .accessDeniedHandler(customAcccessDeniedHandler()))
				.build();
	}
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();

	    config.setAllowedOrigins(List.of("http://localhost:5173","https://fintrack-frontend-taupe.vercel.app"));
	    config.setAllowedMethods(
	        List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
	    );
	    config.setAllowedHeaders(List.of("*"));
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source =
	            new UrlBasedCorsConfigurationSource();

	    source.registerCorsConfiguration("/**", config);

	    return source;
	}	
	@Bean 
	public JWTAuthenticationFilter jwtAuthenticationFilter()
	{
		return new JWTAuthenticationFilter(userDetailsService(),jwtservice());
	}
	@Bean 
	public UserDetailsService userDetailsService() 
	{	
		return new UsersDetailsService();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() 
	{
		DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailsService());
		provider.setPasswordEncoder(passwordencoder());
		return provider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
	{
		return config.getAuthenticationManager();
	}

	
}
