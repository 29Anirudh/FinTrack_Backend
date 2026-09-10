package com.financialtracker.backend.Models.DL.Services;

import com.financialtracker.backend.DTO.DashboardDetails.OverviewDTO;
import com.financialtracker.backend.DTO.Profile.MyProfileDTO;

public interface IProfileServiceDL {
    MyProfileDTO getMyProfile(String username);
    OverviewDTO getOverviewOfBankAccountsOfUser(String username);
}
