package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financialtracker.backend.DTO.Friendship.FriendRequest;
import com.financialtracker.backend.DTO.Friendship.FriendRequestReceived;
import com.financialtracker.backend.DTO.Friendship.FriendRequestSent;
import com.financialtracker.backend.Exceptions.UserDefinedException;
import com.financialtracker.backend.Models.DL.Services.IFriendShipsDL;
import com.financialtracker.backend.Models.POJO.FriendRequests;
import com.financialtracker.backend.Models.POJO.Friendships;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.AccountRepository;
import com.financialtracker.backend.Models.Repositories.FriendRequestsRepository;
import com.financialtracker.backend.Models.Repositories.FriendshipsRepository;
import com.financialtracker.backend.Models.Repositories.UsersRepository;
import com.financialtracker.backend.enums.FriendshipStatus;

@Service 
public class FriendshipsDL implements IFriendShipsDL{

    @Autowired 
    FriendshipsRepository friendshipsRepository;
    @Autowired 
    FriendRequestsRepository friendRequestsRepository;
    @Autowired 
    AccountRepository accountRepository;
    @Autowired 
    UsersRepository usersRepository;

    @Transactional 
	@Override
	public String createRequest(FriendRequest request,String sendersusername) {
        Users sender=usersRepository.findByEmail(sendersusername).orElseThrow(()->new UserDefinedException("No user found with email: "+sendersusername));
        Users receiver=usersRepository.findByEmail(request.receiversemail()).orElseThrow(()->new UserDefinedException("No user found with email: "+request.receiversemail()));
        
		if(sendersusername==request.receiversemail()){
            throw new UserDefinedException("You cannot be friends with you.");
        }
        boolean alreadyFriends=friendshipsRepository.existsByUser1AndUser2(sender, receiver)||friendshipsRepository.existsByUser1AndUser2(receiver, sender);
        if(alreadyFriends){
            throw new UserDefinedException("You are already a friend of "+receiver.getName());
        }
        else if(friendRequestsRepository.existsBySenderAndReceiverAndStatus(sender, receiver,FriendshipStatus.PENDING)){
            throw new UserDefinedException("Already friend request exists.Please wait until "+receiver.getName()+" accepts the request");
        }
        else if(friendRequestsRepository.existsBySenderAndReceiverAndStatus(receiver, sender,FriendshipStatus.PENDING)){
            throw new UserDefinedException("There is a pending request from "+receiver.getName()+".Please accept it.");
        }
        else{
            FriendRequests newRequest=new FriendRequests();
            newRequest.setSender(sender);
            newRequest.setReceiver(receiver);
            newRequest.setStatus(FriendshipStatus.PENDING);
            friendRequestsRepository.save(newRequest);
            return "Request has been sent.Please wait for acceptance";
        }
	}

    @Transactional 
    @Override
    public String acceptRequest(FriendRequest request, String acceptorsUsername) {
        Users acceptor=usersRepository.findByEmail(acceptorsUsername).orElseThrow(()->new UserDefinedException("No user found with email: "+acceptorsUsername));
        Users sender=usersRepository.findByEmail(request.receiversemail()).orElseThrow(()->new UserDefinedException("No user found with email: "+request.receiversemail()));
        
        boolean alreadyFriends=friendshipsRepository.existsByUser1AndUser2(acceptor, sender)||friendshipsRepository.existsByUser1AndUser2(sender, acceptor);
        if(alreadyFriends){
            throw new UserDefinedException("You are already a friend of "+sender.getName());
        }
        else if(friendRequestsRepository.existsBySenderAndReceiverAndStatus(acceptor,sender,FriendshipStatus.PENDING)){
            throw new UserDefinedException("You cannot accept a request sent by you.");
        }
        else{
            Friendships newFriendship=new Friendships();
            FriendRequests friendRequest=friendRequestsRepository.findBySenderAndReceiver(sender, acceptor).orElseThrow(()->new UserDefinedException("No friendrequest exists between you and "+sender.getName()));
            friendRequest.setStatus(FriendshipStatus.ACCEPTED);

            newFriendship.setUser1(sender);
            newFriendship.setUser2(acceptor);
            
            friendshipsRepository.save(newFriendship);
            return "You and "+sender.getName()+" are friends now.";
        }
    }

    @Transactional 
    @Override
    public String rejectRequest(FriendRequest request, String acceptorsUsername) {
        Users acceptor=usersRepository.findByEmail(acceptorsUsername).orElseThrow(()->new UserDefinedException("No user found with email: "+acceptorsUsername));
        Users sender=usersRepository.findByEmail(request.receiversemail()).orElseThrow(()->new UserDefinedException("No user found with email: "+request.receiversemail()));
        
        boolean alreadyFriends=friendshipsRepository.existsByUser1AndUser2(acceptor, sender)||friendshipsRepository.existsByUser1AndUser2(sender, acceptor);
        if(alreadyFriends){
            throw new UserDefinedException("You are already a friend of "+sender.getName());
        }
        else if(friendRequestsRepository.existsBySenderAndReceiverAndStatus(acceptor,sender,FriendshipStatus.PENDING)){
            throw new UserDefinedException("You cannot reject a request sent by you.");
        }
        else{
            FriendRequests friendRequest=friendRequestsRepository.findBySenderAndReceiver(sender, acceptor).orElseThrow(()->new UserDefinedException("No friendrequest exists between you and "+sender.getName()));
            friendRequest.setStatus(FriendshipStatus.REJECTED);
            return "Rejected "+sender.getName();
        }
    }

    @Transactional 
	@Override
	public String cancelRequest(FriendRequest request, String sendersUsername) {
		Users sender=usersRepository.findByEmail(sendersUsername).orElseThrow(()->new UserDefinedException("No user found with email: "+sendersUsername));
        Users receiver=usersRepository.findByEmail(request.receiversemail()).orElseThrow(()->new UserDefinedException("No user found with email: "+request.receiversemail()));

        boolean alreadyFriends=friendshipsRepository.existsByUser1AndUser2(sender, receiver)||friendshipsRepository.existsByUser1AndUser2(receiver, sender);
        if(alreadyFriends){
            throw new UserDefinedException("You are already a friend of "+receiver.getName()+".You can delete your friend.");
        }
        else{
            FriendRequests friendRequest=friendRequestsRepository.findBySenderAndReceiver(sender, receiver).orElseThrow(()->new UserDefinedException("No friend request exists between you and "+receiver.getName()));
            friendRequestsRepository.delete(friendRequest);
            return "Cancelled request successfully.";
        }

	}

	@Override
	public String reRequest(FriendRequest request, String sendersUsername) {
		Users sender=usersRepository.findByEmail(sendersUsername).orElseThrow(()->new UserDefinedException("No user found with email: "+sendersUsername));
        Users receiver=usersRepository.findByEmail(request.receiversemail()).orElseThrow(()->new UserDefinedException("No user found with email: "+request.receiversemail()));

        boolean alreadyFriends=friendshipsRepository.existsByUser1AndUser2(sender, receiver)||friendshipsRepository.existsByUser1AndUser2(receiver, sender);
        FriendRequests friendRequest=friendRequestsRepository.findBySenderAndReceiver(sender,receiver).orElseThrow(()->new UserDefinedException("No friend request exists between you and "+receiver.getName()));
        if(alreadyFriends){
            throw new UserDefinedException("You are already a friend of "+receiver.getName());
        }
        else if(friendRequestsRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendshipStatus.PENDING)){
            friendRequest.setUpdatedAt(LocalDateTime.now());
            return "Request is still under PENDING status.";
        }
        else{
            friendRequest.setStatus(FriendshipStatus.PENDING);
            friendRequest.setUpdatedAt(LocalDateTime.now());
            return "Re-Request successful";
        }
	}

    @Transactional 
	@Override
	public String removeFriend(FriendRequest request, String sendersUsername) {
		Users sender=usersRepository.findByEmail(sendersUsername).orElseThrow(()->new UserDefinedException("No user found with email: "+sendersUsername));
        Users receiver=usersRepository.findByEmail(request.receiversemail()).orElseThrow(()->new UserDefinedException("No user found with email: "+request.receiversemail()));

        Friendships friendship=friendshipsRepository.findByUser1AndUser2(sender, receiver)
                .or(()->friendshipsRepository.findByUser1AndUser2(receiver, sender))
                .orElseThrow(()->new UserDefinedException("You and "+receiver.getName()+" are not friends yet."));
        FriendRequests friendRequest=friendRequestsRepository.findBySenderAndReceiver(sender, receiver).or(()->friendRequestsRepository.findBySenderAndReceiver(receiver,sender)).orElseThrow(()->new UserDefinedException("No friend request exists between you."));
        friendRequestsRepository.delete(friendRequest);
        friendshipsRepository.delete(friendship);
        return "Friend deleted successfully";
	}

	@Override
	public List<FriendRequestReceived> getReceivedRequests(String username) {
        Users user=usersRepository.findByEmail(username).orElseThrow(()->new UserDefinedException("No user found with email: "+username));
		return friendRequestsRepository.findByReceiverAndStatus(user,FriendshipStatus.PENDING).stream().map((fr)->new FriendRequestReceived(fr.getSender().getName(), fr.getSender().getUsername())).toList();
	}

	@Override
	public List<FriendRequestSent> getSentRequests(String username) {
		 Users user=usersRepository.findByEmail(username).orElseThrow(()->new UserDefinedException("No user found with email: "+username));
		return friendRequestsRepository.findBySenderAndStatus(user,FriendshipStatus.PENDING).stream().map((fr)->new FriendRequestSent(fr.getSender().getName(), fr.getSender().getUsername(),fr.getStatus().name())).toList();
	}

    

}
