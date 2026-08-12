package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.financialtracker.backend.Models.POJO.Users;

public class UsersDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private List<GrantedAuthority> role;
	
	

	public UsersDetails(Users U) {
		super();
		this.username = U.getEmail();
		this.password = U.getPassword();
		this.role = Arrays.stream(U.getStatus().split(",")).map(r->new SimpleGrantedAuthority("ROLE_"+r)).collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return this.role;
	}

	@Override
	public @Nullable String getPassword() {
		// TODO Auto-generated method stub
		return this.password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.username;
	}

}
