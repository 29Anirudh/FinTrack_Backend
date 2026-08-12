package com.financialtracker.backend.Controllers;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financialtracker.backend.DTO.UserDTO;
import com.financialtracker.backend.DTO.UserReturnDTO;
import com.financialtracker.backend.Models.BL.UsersBL;
import com.financialtracker.backend.Models.DL.ServicesImpl.JWTService;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.UsersRepository;

@RestController
@RequestMapping("/user")
public class UsersController {
	@Autowired
	UsersBL usersbl;
	@Autowired
	UsersRepository usersrepository;
	@Autowired
	PasswordEncoder passwordencoder;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	JWTService jwtservice;
	Map<String, Object> resp=new HashMap<String, Object>();
	
	public String getUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllUsers(){
		List<UserReturnDTO> listUsers= usersbl.getAllusers();
		resp.clear();
		if(listUsers.isEmpty()) {
			resp.put("msg", "No users found");
			return new ResponseEntity<>(resp,HttpStatus.NOT_FOUND);
		}
		else {
			resp.put("data", listUsers);
			return new ResponseEntity<>(resp,HttpStatus.OK);
		}
	}
	
	@GetMapping("/getuser")
	public ResponseEntity<?> getUserByToken(){
		resp.clear();
		resp.put("data", usersbl.getUserByToken(getUsername()));
		return ResponseEntity.ok(resp);
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody UserDTO U) {
		resp.clear();
		String msg=usersbl.createUser(U);
		resp.put("msg", msg);
		if(msg.contains("exists"))
			return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
		else
			return new ResponseEntity<>(resp,HttpStatus.CREATED);
	}
	
	@PostMapping("/validate")
	public ResponseEntity<?> validateUser(@RequestBody UserDTO U){
		resp.clear();
		Optional<Users> Uold=usersrepository.findByEmail(U.getEmail());
		if(Uold.isPresent()) {
			if(passwordencoder.matches(U.getPassword(), Uold.get().getPassword())) {
				Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(U.getEmail(), U.getPassword()));
				if(authentication.isAuthenticated()) {
					resp.put("role", Uold.get().getStatus());
					resp.put("token", jwtservice.generateToken(Uold.get().getEmail(), Uold.get().getStatus()));
					return new ResponseEntity<>(resp,HttpStatus.OK);
				}
				else {
					resp.put("msg", "Unable to authenticate");
					return new ResponseEntity<>(resp,HttpStatus.NOT_ACCEPTABLE);
				}
			}
			else {
				resp.put("msg", "Password is not correct.");
				return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
			}
		}
		else {
			resp.put("msg", "No user found with email: "+U.getEmail());
			return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
		}
	}
}
