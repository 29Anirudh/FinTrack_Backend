package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financialtracker.backend.DTO.DashboardDetails.OverviewDTO;
import com.financialtracker.backend.DTO.Profile.MyProfileDTO;
import com.financialtracker.backend.Exceptions.UserDefinedException;
import com.financialtracker.backend.Models.DL.Services.IProfileServiceDL;
import com.financialtracker.backend.Models.POJO.Account;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.TransactionRepository;
import com.financialtracker.backend.Models.Repositories.UsersRepository;

@Service
public class ProfileServiceDL implements IProfileServiceDL {

    @Autowired
    UsersRepository userRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public MyProfileDTO getMyProfile(String username) {
        Users me=userRepository.findByEmail(username).orElseThrow(()->new UserDefinedException("No user found with email "+username));
        return new MyProfileDTO(me.getName(), me.getEmail(), me.isVerified());
    }

    @Override
    public OverviewDTO getOverviewOfBankAccountsOfUser(String username) {
        Users me=userRepository.findByEmail(username).orElseThrow(()->new UserDefinedException("No user found with email "+username));
        BigDecimal totalBalance = BigDecimal.ZERO;
        Integer numberOfTransactions = 0;
        for (Account account : me.getAccounts()) {
            totalBalance = totalBalance.add(account.getBalance());
            numberOfTransactions += transactionRepository.getAllTransactionsByAccountno(account.getAccountno()).size();
        }
        return new OverviewDTO(me.getAccounts().size(),totalBalance,numberOfTransactions);
    }
    
}
