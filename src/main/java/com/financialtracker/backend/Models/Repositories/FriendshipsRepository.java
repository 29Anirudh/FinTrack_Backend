package com.financialtracker.backend.Models.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financialtracker.backend.Models.POJO.Friendships;
import com.financialtracker.backend.Models.POJO.Users;

@Repository 
public interface FriendshipsRepository extends JpaRepository<Friendships,Long> {
    Boolean existsByUser1AndUser2(Users user1,Users user2);
    Optional<Friendships> findByUser1AndUser2(Users user1,Users user2);
    List<Friendships> findByUser1OrUser2(Users user1,Users user2);
    
}
