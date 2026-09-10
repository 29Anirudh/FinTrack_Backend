package com.financialtracker.backend.Controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financialtracker.backend.Models.BL.ProfileBL;

@RestController
@RequestMapping("/me")
public class ProfileController {
    @Autowired
    ProfileBL profileBL;

    private String getUsername() {
		return SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getName();
	}

    @GetMapping
    public ResponseEntity<?> getProfileDetails(){
        Map<String,Object> resp=new HashMap<>();
        resp.put("userDetails", profileBL.getBasicProfileOfUser(getUsername()));
        resp.put("overviewDetails", profileBL.getOverviewOfAccountsOfUser(getUsername()));
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }
}
