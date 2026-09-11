package com.financialtracker.backend.Models.DL.ServicesImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financialtracker.backend.DTO.SplitExpense.EachUserPaymentRequest;
import com.financialtracker.backend.DTO.SplitExpense.SplitRequest;
import com.financialtracker.backend.Exceptions.UserDefinedException;
import com.financialtracker.backend.Models.DL.Services.IExpenseSplitServiceDL;
import com.financialtracker.backend.Models.POJO.ExpenseSplit;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.EachUserPaymentRepository;
import com.financialtracker.backend.Models.Repositories.ExpenseSplitRepository;
import com.financialtracker.backend.Models.Repositories.FriendshipsRepository;
import com.financialtracker.backend.Models.Repositories.UsersRepository;

import jakarta.transaction.Transactional;

@Service 
public class ExpenseSplitServiceDL implements IExpenseSplitServiceDL{
    @Autowired 
    EachUserPaymentRepository eachUserPaymentRepository;
    @Autowired 
    ExpenseSplitRepository expenseSplitRepository;
    @Autowired 
    UsersRepository usersRepository;
    @Autowired 
    FriendshipsRepository friendshipsRepository;
    
    @Transactional 
    @Override
    public String createExpenseSplit(SplitRequest splitRequest,String createdByUsername) {
        ExpenseSplit newSplit=new ExpenseSplit();
        Users splitCreator=usersRepository.findByEmail(createdByUsername).orElseThrow(()->new UserDefinedException("No user found with mail :"+createdByUsername));
        for (EachUserPaymentRequest eachUserPaymentRequest : splitRequest.eachUserPayments()) {
            boolean alreadyFriends=friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(createdByUsername,eachUserPaymentRequest.personUsername())
                    ||
                    friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(createdByUsername, eachUserPaymentRequest.personUsername());
        }
        newSplit.setCreatedBy(splitCreator);
        if(createdByUsername!=splitRequest.ownerUsername()){
            // Users splitOwner=usersRepository.findByEmail(createdByUsername)
        }
        return "";
    }

}
