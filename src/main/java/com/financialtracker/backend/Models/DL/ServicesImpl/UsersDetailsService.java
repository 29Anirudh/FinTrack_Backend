package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.UsersRepository;

public class UsersDetailsService implements UserDetailsService {
	@Autowired
	UsersRepository usersrepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Users> user=usersrepository.findByEmail(username);
		return user.map(UsersDetails::new).orElseThrow(()->new UsernameNotFoundException("No user found with email "+username));
	}

}
