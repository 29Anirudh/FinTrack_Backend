package com.financialtracker.backend.Models.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financialtracker.backend.Models.POJO.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
	public Optional<Users> findByEmail(String email);
	Boolean existsByUsernameIgnoreCase(String username);
	List<Users> findByUsernameContainsIgnoreCaseOrNameContainingIgnoreCase(String username,String name);
}
