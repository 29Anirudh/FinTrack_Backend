package com.financialtracker.backend.Models.DL.Services;

import java.util.List;


import com.financialtracker.backend.DTO.UserDTO;
import com.financialtracker.backend.DTO.UserReturnDTO;

public interface IUsersServiceDL {
	List<UserReturnDTO> getAllusers();
	UserReturnDTO getUserById(int id);
	Boolean validateUser(UserDTO U);
	String createUser(UserDTO U);
	UserReturnDTO getUserByToken(String username);
}
