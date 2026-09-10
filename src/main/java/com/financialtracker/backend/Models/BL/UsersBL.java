package com.financialtracker.backend.Models.BL;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import com.financialtracker.backend.DTO.UserDTO;
import com.financialtracker.backend.DTO.UserReturnDTO;
import com.financialtracker.backend.Models.DL.ServicesImpl.UsersSeviceDL;

public class UsersBL {
	@Autowired
	UsersSeviceDL usersdl;
	
	public List<UserReturnDTO> getAllusers(){
		return usersdl.getAllusers();
	}
	
	public UserReturnDTO getUserById(int id) {
		return usersdl.getUserById(id);
	}
	
	public Boolean validateUser(UserDTO U) {
		return usersdl.validateUser(U);
	}
	
	public String createUser(UserDTO U) {
		return usersdl.createUser(U);
	}
	
	public UserReturnDTO getUserByToken(String username) {
		return usersdl.getUserByToken(username);
	}

	public String searchForNewUsername(String newUsername){
		return usersdl.searchForNewUsername(newUsername);
	}

	public String setUsername(String newUsername,String email){
		return usersdl.setUsername(newUsername, email);
	}
}
