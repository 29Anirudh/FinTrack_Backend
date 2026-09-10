package com.financialtracker.backend.Controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financialtracker.backend.DTO.Friendship.FriendRequest;
import com.financialtracker.backend.Models.BL.FriendshipBL;

@RestController 
@RequestMapping("/friends")
public class FriendshipController {
    @Autowired 
    FriendshipBL friendshipBL;

    private String getUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/createrequest")
    public ResponseEntity<?> createFriendRequest(@RequestBody FriendRequest friendRequest){
        Map<String,String> response=new HashMap<>();
        response.put("msg", friendshipBL.createRequest(friendRequest, getUsername()));
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PostMapping("/acceptrequest")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendRequest friendRequest){
        Map<String,String> response=new HashMap<>();
        response.put("msg", friendshipBL.acceptRequest(friendRequest, getUsername()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/rejectrequest")
    public ResponseEntity<?> rejectFriendRequest(@RequestBody FriendRequest friendRequest){
        Map<String,String> response=new HashMap<>();
        response.put("msg", friendshipBL.rejectRequest(friendRequest, getUsername()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/rerequest")
    public ResponseEntity<?> reRequest(@RequestBody FriendRequest friendRequest){
        Map<String,String> response=new HashMap<>();
        response.put("msg", friendshipBL.reRequest(friendRequest, getUsername()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/removefriend")
    public ResponseEntity<?> removeFriend(@RequestBody FriendRequest friendRequest){
        Map<String,String> response=new HashMap<>();
        response.put("msg", friendshipBL.removeFriend(friendRequest, getUsername()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/cancelrequest")
    public ResponseEntity<?> cancelRequest(@RequestBody FriendRequest friendRequest){
        Map<String,String> response=new HashMap<>();
        response.put("msg", friendshipBL.cancelRequest(friendRequest, getUsername()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getAllRequests(){
        Map<String,Object> response=new HashMap<>();
        response.put("sentRequests", friendshipBL.getMyFriendRequestSents(getUsername()));
        response.put("receivedRequests", friendshipBL.getFriendRequestReceived(getUsername()));
        return ResponseEntity.ok(response);
    }

    

}
