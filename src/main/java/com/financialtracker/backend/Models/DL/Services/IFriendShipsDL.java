package com.financialtracker.backend.Models.DL.Services;

import java.util.List;

import com.financialtracker.backend.DTO.Friendship.FriendRequest;
import com.financialtracker.backend.DTO.Friendship.FriendRequestReceived;
import com.financialtracker.backend.DTO.Friendship.FriendRequestSent;

public interface IFriendShipsDL {
    String createRequest(FriendRequest request,String sendersusername);
    String acceptRequest(FriendRequest request,String sendersusername);
    String rejectRequest(FriendRequest request,String sendersusername);
    String cancelRequest(FriendRequest request,String sendersUsername);
    String reRequest(FriendRequest request,String sendersUsername);
    String removeFriend(FriendRequest request,String sendersUsername);

    List<FriendRequestReceived> getReceivedRequests(String username);
    List<FriendRequestSent> getSentRequests(String username);
    
}
