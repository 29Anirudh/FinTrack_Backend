package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financialtracker.backend.DTO.SplitExpense.EachUserPaymentRequest;
import com.financialtracker.backend.DTO.SplitExpense.SplitRequest;
import com.financialtracker.backend.Exceptions.UserDefinedException;
import com.financialtracker.backend.Models.DL.Services.IExpenseSplitServiceDL;
import com.financialtracker.backend.Models.POJO.EachUserPayment;
import com.financialtracker.backend.Models.POJO.ExpenseSplit;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.EachUserPaymentRepository;
import com.financialtracker.backend.Models.Repositories.ExpenseSplitRepository;
import com.financialtracker.backend.Models.Repositories.FriendshipsRepository;
import com.financialtracker.backend.Models.Repositories.UsersRepository;
import com.financialtracker.backend.enums.SplitStatus;

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
    public String createExpenseSplit(SplitRequest splitRequest,String createdByEmail) {
        ExpenseSplit newSplit=new ExpenseSplit();
        Users splitCreator=usersRepository.findByEmail(createdByEmail).orElseThrow(()->new UserDefinedException("No user found with mail: "+createdByEmail));
        if(!checkWhetherFriends(splitRequest.eachUserPayments(), splitCreator.getUsername())){
            throw new UserDefinedException("One of the members in the Split Request is not friends with "+splitCreator.getUsername());
        }
        if(splitCreator.getUsername()==splitRequest.ownerUsername()){
            newSplit.setOwner(splitCreator);
        }
        else{
            Users splitOwner=usersRepository.findByUsernameIgnoreCase(splitRequest.ownerUsername()).orElseThrow(()->new UserDefinedException("No user found with username: "+splitRequest.ownerUsername()));
            if(!checkWhetherFriends(splitRequest.eachUserPayments(), splitOwner.getUsername())){
                throw new UserDefinedException("One of the members in the Split Request is not friends with "+splitOwner.getUsername());
            }
            boolean creatorAndOwnerAlreadyFriends=friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(splitCreator.getUsername(),splitOwner.getUsername())
                    ||
                    friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(splitOwner.getUsername(), splitCreator.getUsername());
            if(creatorAndOwnerAlreadyFriends){
                throw new UserDefinedException("Split Paid person is not friends with you.");
            }
            newSplit.setOwner(splitOwner);
        }
        newSplit.setCreatedBy(splitCreator);
        newSplit.setAmount(splitRequest.amount());
        newSplit.setCountOfMembers(splitRequest.numberOfPeople());
        newSplit.setDateOfExpense(splitRequest.dateOfExpense());
        newSplit.setDescription(splitRequest.purpose());
        newSplit.setSplitMode(splitRequest.mode());
        newSplit.setStatus(SplitStatus.CREATED);
        for (EachUserPaymentRequest eachUserInSplitRequest : splitRequest.eachUserPayments()) {
            EachUserPayment eachUserPayment=new EachUserPayment();
            eachUserPayment.
        }

        
        return "";
    }

    private boolean checkWhetherFriends(List<EachUserPaymentRequest> requestsListInASplit,String username){
        for (EachUserPaymentRequest eachUserPaymentRequest : requestsListInASplit) {
            boolean alreadyFriends=friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(username,eachUserPaymentRequest.personUsername())
                    ||
                    friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(username, eachUserPaymentRequest.personUsername());
            if(!alreadyFriends)
                throw new UserDefinedException(username+" and "+eachUserPaymentRequest.personUsername()+" are not friends.");
        }
        return true;
    }

}
