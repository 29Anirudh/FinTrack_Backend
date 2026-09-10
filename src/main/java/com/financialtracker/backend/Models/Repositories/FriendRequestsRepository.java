package com.financialtracker.backend.Models.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.financialtracker.backend.Models.POJO.FriendRequests;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.enums.FriendshipStatus;

@Repository 
public interface FriendRequestsRepository extends JpaRepository<FriendRequests,Long> {
    Boolean existsBySenderAndReceiverAndStatus(Users sender,Users receiver,FriendshipStatus status);
    Optional<FriendRequests> findBySenderAndReceiver(Users sender,Users receiver);
    List<FriendRequests> findByReceiverAndStatus(Users receiver,FriendshipStatus status); //received friend requests
    List<FriendRequests> findBySenderAndStatus(Users sender,FriendshipStatus status); //sent friend requests
}
