package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financialtracker.backend.DTO.UserDTO;
import com.financialtracker.backend.DTO.UserReturnDTO;
import com.financialtracker.backend.DTO.User.UsersDetailsForRequests;
import com.financialtracker.backend.Exceptions.UserDefinedException;
import com.financialtracker.backend.Models.DL.Services.IUsersServiceDL;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.FriendRequestsRepository;
import com.financialtracker.backend.Models.Repositories.UsersRepository;
import com.financialtracker.backend.enums.FriendshipStatus;


@Service
public class UsersSeviceDL implements IUsersServiceDL {
	@Autowired
	UsersRepository usersrepository;
	@Autowired
	PasswordEncoder passwordencoder;
	@Autowired 
	FriendRequestsRepository friendRequestsRepository;
	@Override
	public List<UserReturnDTO> getAllusers() {
		List<Users> users= usersrepository.findAll();
		return users.stream().map(
			user->new UserReturnDTO(
					user.getUserid(),
					user.getAccounts().stream()
						.map(acc->acc.getAccountno()).toList(),
					user.getName(),
					user.getEmail(),
					user.getDateofbirth(),
					user.getStatus(),
					user.getCreatedAt()))
			.toList();
	}

	@Override
	public UserReturnDTO getUserById(int id) {
		Users user=usersrepository.findById(id).orElseThrow(()->new UserDefinedException("No user found with id "+id));
		return new UserReturnDTO(user.getUserid(),user.getAccounts().stream().map(t ->t.getAccountno()).toList(),user.getName(),user.getEmail(),user.getDateofbirth(),user.getStatus(),user.getCreatedAt());
	}

	@Override
	public Boolean validateUser(UserDTO U) {
		Optional<Users> Uold=usersrepository.findByEmail(U.getEmail());
		if(Uold.isPresent() && passwordencoder.matches(U.getPassword(), Uold.get().getPassword())) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String createUser(UserDTO U) {
		Optional<Users> Uold=usersrepository.findByEmail(U.getEmail());
		if(Uold.isEmpty()) {
			Users Unew=new Users();
			Unew.setName(U.getName());
			Unew.setEmail(U.getEmail());
			Unew.setPassword(passwordencoder.encode(U.getPassword()));
			Unew.setDateofbirth(U.getDateofbirth());
			Unew.setStatus(U.getStatus());
			usersrepository.save(Unew);
			return "USER "+ U.getName()+ " saved successfully.";
		
		}
		else {
			return "User already exists with email "+U.getEmail();
		}
		
	}

	@Override
	public UserReturnDTO getUserByToken(String username) {
		Users U=usersrepository.findByEmail(username).orElseThrow(()->new UserDefinedException("No user exists with email: "+username));
		return new UserReturnDTO(U.getUserid(),U.getAccounts().stream().map(t->t.getAccountno()).toList(),U.getName(),U.getEmail(),U.getDateofbirth(),U.getStatus(),U.getCreatedAt());
	}

	@Override
	public String searchForNewUsername(String searchValue) {
		if(searchValue.length()<6){
			throw new UserDefinedException("Length should be atleast 6.");
		}
		else if(usersrepository.existsByUsernameIgnoreCase(searchValue.toLowerCase())){
			throw new UserDefinedException("\'"+searchValue+"\' already exists");
		}
		else{
			return "\'"+searchValue+"\' is available";
		}
	}

	@Transactional 
	@Override
	public String setUsername(String newUsername, String email) {
		Users user=usersrepository.findByEmail(email).orElseThrow(()-> new UserDefinedException("No user exists with the "+email));
		if(!searchForNewUsername(newUsername).contains("available")){
			throw new UserDefinedException("\'"+newUsername+"\' already exists.Please choose other username.");
		}
		else{
			user.setUsername(newUsername);
			return "Username set successfully.";
		}
	}

	@Transactional
	@Override
	public List<UsersDetailsForRequests> getSearchResultsOfUsers(String searchKey, String username) {
		List<Users> usersList=usersrepository.findByUsernameContainsIgnoreCaseOrNameContainingIgnoreCase(searchKey,searchKey);
		Users me=usersrepository.findByEmail(username).orElseThrow(()->new UserDefinedException("No user exists with email: "+username));
		usersList.remove(me);
		usersList.removeIf(user->user.getUsername()==null || user.getUsername().length()==0);

		List<UsersDetailsForRequests> userDetails=new ArrayList<>();
		for (Users user : usersList) {
			if(friendRequestsRepository.existsBySenderUsernameIgnoreCaseAndReceiverUsernameIgnoreCaseAndStatus(me.getUsername(), user.getUsername(),FriendshipStatus.ACCEPTED) || friendRequestsRepository.existsBySenderUsernameIgnoreCaseAndReceiverUsernameIgnoreCaseAndStatus(user.getUsername(), me.getUsername(), FriendshipStatus.ACCEPTED)){
				System.out.println(user.getUsername());
				userDetails.add(new UsersDetailsForRequests(user.getUserid(),user.getName(),user.getEmail(), user.getUsername(), "ACCEPTED"));
			}
			else if(friendRequestsRepository.existsBySenderUsernameIgnoreCaseAndReceiverUsernameIgnoreCaseAndStatus(me.getUsername(), user.getUsername(),FriendshipStatus.PENDING)){
				userDetails.add(new UsersDetailsForRequests(user.getUserid(), user.getName(),user.getEmail(), user.getUsername(), "SENT"));
			}
			else if(friendRequestsRepository.existsBySenderUsernameIgnoreCaseAndReceiverUsernameIgnoreCaseAndStatus(user.getUsername(),me.getUsername(),FriendshipStatus.PENDING)){
				userDetails.add(new UsersDetailsForRequests(user.getUserid(), user.getName(),user.getEmail(), user.getUsername(), "REQUESTED"));
			}
			else if(friendRequestsRepository.existsBySenderUsernameIgnoreCaseAndReceiverUsernameIgnoreCaseAndStatus(me.getUsername(), user.getUsername(), FriendshipStatus.REJECTED)){
				userDetails.add(new UsersDetailsForRequests(user.getUserid(), user.getName(),user.getEmail(), user.getUsername(), "REJECTED"));
			}
			else{
				userDetails.add(new UsersDetailsForRequests(user.getUserid(), user.getName(), user.getEmail(),user.getUsername(), ""));
			}
		}
		return userDetails;
	}
	
}
