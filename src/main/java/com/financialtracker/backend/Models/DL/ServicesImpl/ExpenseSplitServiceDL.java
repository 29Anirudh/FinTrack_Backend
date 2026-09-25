package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financialtracker.backend.DTO.SplitExpense.EachUserPaymentRequest;
import com.financialtracker.backend.DTO.SplitExpense.EachUserPaymentResponse;
import com.financialtracker.backend.DTO.SplitExpense.SplitRequest;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseBasic;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseMain;
import com.financialtracker.backend.Exceptions.UserDefinedException;
import com.financialtracker.backend.Models.DL.Services.IExpenseSplitServiceDL;
import com.financialtracker.backend.Models.POJO.EachUserPayment;
import com.financialtracker.backend.Models.POJO.ExpenseSplit;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.EachUserPaymentRepository;
import com.financialtracker.backend.Models.Repositories.ExpenseSplitRepository;
import com.financialtracker.backend.Models.Repositories.FriendshipsRepository;
import com.financialtracker.backend.Models.Repositories.UsersRepository;
import com.financialtracker.backend.enums.EachPaymentStatus;
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
        Users splitOwner=usersRepository.findByUsernameIgnoreCase(splitRequest.ownerUsername()).orElseThrow(()->new UserDefinedException("No user found with username: "+splitRequest.ownerUsername()));
        checkWhetherFriends(splitRequest.eachUserPayments(), splitCreator.getUsername());
        if(!checkBalanceEquality(splitRequest.eachUserPayments(), splitRequest.amount())){
            throw new UserDefinedException("Total expense amount does not match the sum of each amounts.");
        }
        if(splitRequest.eachUserPayments().size()!=splitRequest.numberOfPeople()){
            throw new UserDefinedException("Number of people in split doesn't match the members.");
        }
        if(splitCreator.getUsername().equalsIgnoreCase(splitRequest.ownerUsername())){
            newSplit.setOwner(splitCreator);
        }
        else{
            checkWhetherFriends(splitRequest.eachUserPayments(), splitOwner.getUsername());
            boolean creatorAndOwnerAlreadyFriends=friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(splitCreator.getUsername(),splitOwner.getUsername())
                    ||
                    friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(splitOwner.getUsername(), splitCreator.getUsername());
            if(!creatorAndOwnerAlreadyFriends){
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
        List<EachUserPayment> eachUserPayments=new ArrayList<>();
        for (EachUserPaymentRequest eachUserInSplitRequest : splitRequest.eachUserPayments()) {
            EachUserPayment eachUserPayment=new EachUserPayment();
            // if(eachUserInSplitRequest.personUsername().equalsIgnoreCase(splitOwner.getUsername())){
            //     if(!splitRequest.isTransactionThere()){
            //         Transactions trans_new=new Transactions();
            //         trans_new.setAmount(splitRequest.amount());
            //         trans_new.setCategory(TransactionCategory.SPLIT);
            //         trans_new.setFromAccountno(null);
            //     }
            // }
            Users shareUser=usersRepository.findByUsernameIgnoreCase(eachUserInSplitRequest.personUsername()).orElseThrow(()->new UserDefinedException("No user found with the username: "+eachUserInSplitRequest.personUsername()));
            eachUserPayment.setUser(shareUser);
            eachUserPayment.setEachShareAmount(eachUserInSplitRequest.eachShareAmount());
            eachUserPayment.setExpenseSplit(newSplit);
            eachUserPayment.setPaymentStatus(EachPaymentStatus.PENDING);
            eachUserPayment.setTransaction(null);

            eachUserPayments.add(eachUserPayment);
        }

        newSplit.setEachUserPayments(eachUserPayments);
        
        expenseSplitRepository.save(newSplit);

        
        return "Expense splitted successfully.";
    }

    private void checkWhetherFriends(List<EachUserPaymentRequest> requestsListInASplit,String username){
        for (EachUserPaymentRequest eachUserPaymentRequest : requestsListInASplit) {
            if(eachUserPaymentRequest.personUsername().equalsIgnoreCase(username)){
                continue;
            }
            boolean alreadyFriends=friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(username,eachUserPaymentRequest.personUsername())
                    ||
                    friendshipsRepository.existsByUser1UsernameIgnoreCaseAndUser2UsernameIgnoreCase(eachUserPaymentRequest.personUsername(),username);
            if(!alreadyFriends)
                throw new UserDefinedException(username+" and "+eachUserPaymentRequest.personUsername()+" are not friends.");
        }
    }

    private boolean checkBalanceEquality(List<EachUserPaymentRequest> eachUserPaymentRequests,BigDecimal totalExpense){
        BigDecimal total=BigDecimal.ZERO;
        for (EachUserPaymentRequest eachUserPaymentRequest : eachUserPaymentRequests) {
            total=total.add(eachUserPaymentRequest.eachShareAmount());
        }

        return total.compareTo(totalExpense)==0;

    }

    @Override
    public List<SplitResponseBasic> getBasicSplits(String myEmail) {
        List<ExpenseSplit> expenses=expenseSplitRepository.getAllRelatedExpenses(myEmail);
        List<SplitResponseBasic> basicExpenseSplits=new ArrayList<>();

        for (ExpenseSplit expense : expenses) {
            BigDecimal myShare=expense.getEachUserPayments().stream().filter(payment ->payment.getUser().getEmail().equalsIgnoreCase(myEmail)).map(EachUserPayment::getEachShareAmount).findFirst().orElse(BigDecimal.ZERO);
            String paidBy;
            if(expense.getOwner().getEmail().equalsIgnoreCase(myEmail)){
                paidBy="You";
            }
            else{
                paidBy=expense.getOwner().getName();
            }
            basicExpenseSplits.add(new SplitResponseBasic(expense.getId(),expense.getDescription(),expense.getDateOfExpense(),expense.getAmount(),myShare,paidBy,expense.getCountOfMembers(),expense.getStatus(),expense.getCreatedAt()));
        }
        return basicExpenseSplits;
    }

    @Override
    public SplitResponseMain getExpenseDetail(String myEmail, UUID expenseId) {
        ExpenseSplit expense=expenseSplitRepository.findById(expenseId).orElseThrow(()->new UserDefinedException("No split found with id:"+expenseId));
        if(expense.getEachUserPayments().stream().filter(payment->payment.getUser().getEmail().equalsIgnoreCase(myEmail)).toList().size()==0){
            throw new UserDefinedException("You have no access for the expense with id: "+expenseId);
        }

        List<EachUserPaymentResponse> eachUserPaymentResponses=new ArrayList<>();
        for (EachUserPayment eachUserPayment : expense.getEachUserPayments()) {
            String name;
            
            if(eachUserPayment.getUser().getEmail().equalsIgnoreCase(myEmail)){
                name="You";
            }
            else{
                name=eachUserPayment.getUser().getName();
            }
            
            
            Boolean isOwner=eachUserPayment.getUser().getEmail().equalsIgnoreCase(expense.getOwner().getEmail());
            eachUserPaymentResponses.add(new EachUserPaymentResponse(eachUserPayment.getId(), name, eachUserPayment.getEachShareAmount(), isOwner, eachUserPayment.getPaymentStatus()));
        }
        String paidBy;
        String createdBy;
        if(expense.getOwner().getEmail().equalsIgnoreCase(myEmail)){
                paidBy="You";
        }
        else{
            paidBy=expense.getOwner().getName();
        }
        if(expense.getCreatedBy().getEmail().equalsIgnoreCase(myEmail)){
                createdBy="You";
        }
        else{
            createdBy=expense.getCreatedBy().getName();
        }
        return new SplitResponseMain(expense.getId(),expense.getDescription(),expense.getDateOfExpense(),expense.getAmount(),paidBy,createdBy,eachUserPaymentResponses,expense.getCountOfMembers(),expense.getStatus(),expense.getSplitMode(),expense.getCreatedAt());
    }

}
