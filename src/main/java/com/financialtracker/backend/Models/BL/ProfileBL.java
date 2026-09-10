package com.financialtracker.backend.Models.BL;

import org.springframework.beans.factory.annotation.Autowired;

import com.financialtracker.backend.DTO.DashboardDetails.OverviewDTO;
import com.financialtracker.backend.DTO.Profile.MyProfileDTO;
import com.financialtracker.backend.Models.DL.ServicesImpl.ProfileServiceDL;

public class ProfileBL {
    @Autowired
    ProfileServiceDL profileServiceDL;

    public OverviewDTO getOverviewOfAccountsOfUser(String username){
        return profileServiceDL.getOverviewOfBankAccountsOfUser(username);
    }

    public MyProfileDTO getBasicProfileOfUser(String username){
        return profileServiceDL.getMyProfile(username);
    }
    
}