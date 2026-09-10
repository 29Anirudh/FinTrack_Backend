package com.financialtracker.backend.Models.BL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.financialtracker.backend.DTO.Friendship.FriendRequest;
import com.financialtracker.backend.DTO.Friendship.FriendRequestReceived;
import com.financialtracker.backend.DTO.Friendship.FriendRequestSent;
import com.financialtracker.backend.Models.DL.ServicesImpl.FriendshipsDL;

public class FriendshipBL {
    @Autowired 
    FriendshipsDL friendshipsDL;

    public String createRequest(FriendRequest friendRequest,String senderUsername){
        return friendshipsDL.createRequest(friendRequest, senderUsername);
    }
    public String acceptRequest(FriendRequest friendRequest,String acceptorUsername){
        return friendshipsDL.acceptRequest(friendRequest, acceptorUsername);
    }
    public String rejectRequest(FriendRequest friendRequest,String acceptorUsername){
        return friendshipsDL.rejectRequest(friendRequest, acceptorUsername);
    }
    public String cancelRequest(FriendRequest request,String sendersUsername){
        return friendshipsDL.cancelRequest(request, sendersUsername);
    }
    public String reRequest(FriendRequest request,String sendersUsername){
        return friendshipsDL.reRequest(request, sendersUsername);
    }
    public String removeFriend(FriendRequest request,String sendersUsername){
        return friendshipsDL.removeFriend(request, sendersUsername);
    }
    public List<FriendRequestReceived> getFriendRequestReceived(String myUsername){
        return friendshipsDL.getReceivedRequests(myUsername);
    }
    public List<FriendRequestSent> getMyFriendRequestSents(String myUsername){
        return friendshipsDL.getSentRequests(myUsername);
    }
    
}