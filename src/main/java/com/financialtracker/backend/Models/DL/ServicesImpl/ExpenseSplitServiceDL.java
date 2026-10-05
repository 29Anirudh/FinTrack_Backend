package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.financialtracker.backend.DTO.SplitExpense.EachUserPaymentRequest;
import com.financialtracker.backend.DTO.SplitExpense.EachUserPaymentResponse;
import com.financialtracker.backend.DTO.SplitExpense.PaymentRecord;
import com.financialtracker.backend.DTO.SplitExpense.SplitRequest;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseBasic;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseMain;
import com.financialtracker.backend.Exceptions.UserDefinedException;
import com.financialtracker.backend.Models.DL.Services.IExpenseSplitServiceDL;
import com.financialtracker.backend.Models.POJO.Account;
import com.financialtracker.backend.Models.POJO.EachUserPayment;
import com.financialtracker.backend.Models.POJO.ExpenseSplit;
import com.financialtracker.backend.Models.POJO.Transactions;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.AccountRepository;
import com.financialtracker.backend.Models.Repositories.EachUserPaymentRepository;
import com.financialtracker.backend.Models.Repositories.ExpenseSplitRepository;
import com.financialtracker.backend.Models.Repositories.FriendshipsRepository;
import com.financialtracker.backend.Models.Repositories.TransactionRepository;
import com.financialtracker.backend.Models.Repositories.UsersRepository;
import com.financialtracker.backend.enums.EachPaymentStatus;
import com.financialtracker.backend.enums.SplitStatus;
import com.financialtracker.backend.enums.TransactionCategory;

import jakarta.transaction.Transactional;

@Service 
public class ExpenseSplitServiceDL implements IExpenseSplitServiceDL{
    @Autowired 
    EachUserPaymentRepository eachUserPaymentRepository;
    @Autowired 
    ExpenseSplitRepository expenseSplitRepository;
    @Autowired 
    TransactionRepository transactionRepository;
    @Autowired 
    UsersRepository usersRepository;
    @Autowired 
    FriendshipsRepository friendshipsRepository;
    @Autowired 
    AccountRepository accountRepository;
    
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
        long uniqueUsers = splitRequest.eachUserPayments()
                                        .stream()
                                        .map(EachUserPaymentRequest::personUsername)
                                        .map(String::toLowerCase)
                                        .distinct()
                                        .count();
        if(uniqueUsers!=splitRequest.numberOfPeople()){
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
            if(eachUserInSplitRequest.personUsername().equalsIgnoreCase(splitOwner.getUsername())){
                if(!splitRequest.isTransactionThere()){
                    Transactions trans_new=new Transactions();
                    trans_new.setAmount(splitRequest.amount());
                    trans_new.setCategory(TransactionCategory.SPLIT);
                    trans_new.setFromAccountno(null);
                }
            }
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
            eachUserPaymentResponses.add(new EachUserPaymentResponse(eachUserPayment.getId(), name, eachUserPayment.getUser().getUsername(), eachUserPayment.getEachShareAmount(), isOwner, eachUserPayment.getPaymentStatus()));
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

    @Transactional 
    @Override
    public String confirmPayment(PaymentRecord paymentRecord, String myEmail) {
        Users me=usersRepository.findByEmail(myEmail).orElseThrow(()->new UserDefinedException("No user exists with email: "+myEmail));
        Users user=usersRepository.findByUsernameIgnoreCase(paymentRecord.personsUsername()).orElseThrow(()->new UserDefinedException("No person exists with username: "+paymentRecord.personsUsername()));

        ExpenseSplit split=expenseSplitRepository.findById(paymentRecord.splitId()).orElseThrow(()->new UserDefinedException("No split found with the ID"));
        if(!split.getOwner().getEmail().equalsIgnoreCase(myEmail)){
            throw new UserDefinedException("You cannot confirm payment since you are not the owner.");
        }
        
        
        EachUserPayment paymentOfUser=split.getEachUserPayments().stream().filter(e->e.getUser().getUsername().equalsIgnoreCase(user.getUsername())).findFirst().orElse(null);
        if(paymentOfUser==null){
            throw new UserDefinedException("There is no person with username:"+paymentRecord.personsUsername()+" in the split.");
        }
        if (paymentOfUser.getEachShareAmount().compareTo(paymentRecord.amount()) != 0) {
            throw new UserDefinedException("Amount doesn't match");
        }
        if(paymentOfUser.getPaymentStatus().equals(EachPaymentStatus.AWAITING_CONFIRMATION)){
            paymentOfUser.setPaymentStatus(EachPaymentStatus.PAID);
        }
        else{
            throw new UserDefinedException("Unable to confirm payment that\'s on status: "+paymentOfUser.getPaymentStatus().name());
        }
        if(!paymentRecord.isThereATransaction()){
            Transactions transaction=new Transactions();
            Long accountidO = accountRepository
                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                Integer.parseInt(paymentRecord.maskedAccountno().substring(paymentRecord.maskedAccountno().length() - 4)), me.getEmail())
                                .orElseThrow(() -> new UserDefinedException(
                                                "No account found with accountid " + paymentRecord.maskedAccountno() + " for user "
                                                                + me.getEmail()));
            Account account=accountRepository.findById(accountidO).orElseThrow(() -> new UserDefinedException("No account found with number"));
            account.setBalance(account.getBalance().add(paymentRecord.amount()));
            transaction.setAmount(paymentRecord.amount());
            transaction.setCategory(TransactionCategory.SPLIT);
            transaction.setDescription("[Split Record] "+paymentOfUser.getUser().getName()+" has paid on the split.");
            transaction.setFromAccountno(account);
            transaction.setTransactiontime(LocalDate.now());
            transaction.setType("CREDIT");
            split.addTransaction(transaction);
        }
        return "Payment Confirmed."+paymentRecord.personsUsername()+"\'s share has settled.";
    }

    @Transactional 
    @Override
    public String rejectPayment(PaymentRecord paymentRecord, String myEmail) {
        Users me=usersRepository.findByEmail(myEmail).orElseThrow(()->new UserDefinedException("No user exists with email: "+myEmail));
        Users user=usersRepository.findByUsernameIgnoreCase(paymentRecord.personsUsername()).orElseThrow(()->new UserDefinedException("No person exists with username: "+paymentRecord.personsUsername()));

        ExpenseSplit split=expenseSplitRepository.findById(paymentRecord.splitId()).orElseThrow(()->new UserDefinedException("No split found with the ID"));
        if(!split.getOwner().getEmail().equalsIgnoreCase(me.getEmail())){
            throw new UserDefinedException("You cannot reject payment since you are not owner");
        }
        
        
        EachUserPayment paymentOfUser=split.getEachUserPayments().stream().filter(e->e.getUser().getUsername().equalsIgnoreCase(user.getUsername())).findFirst().orElse(null);
        if(paymentOfUser==null){
            throw new UserDefinedException("There is no person with username:"+paymentRecord.personsUsername()+" in the split.");
        }
        if (paymentOfUser.getEachShareAmount().compareTo(paymentRecord.amount()) != 0) {
            throw new UserDefinedException("Amount doesn't match");
        }
        if(paymentOfUser.getPaymentStatus().equals(EachPaymentStatus.AWAITING_CONFIRMATION)){
            paymentOfUser.setPaymentStatus(EachPaymentStatus.REJECTED);
        }
        else{
            throw new UserDefinedException("Unable to reject payment that\'s on status: "+paymentOfUser.getPaymentStatus().name());
        }
        return "Payment Rejected.";
    }

    @Transactional 
    @Override
    public String MarkAsReceived(PaymentRecord paymentRecord, String myEmail) {
        Users me=usersRepository.findByEmail(myEmail).orElseThrow(()->new UserDefinedException("No user exists with email: "+myEmail));
        Users user=usersRepository.findByUsernameIgnoreCase(paymentRecord.personsUsername()).orElseThrow(()->new UserDefinedException("No person exists with username: "+paymentRecord.personsUsername()));

        ExpenseSplit split=expenseSplitRepository.findById(paymentRecord.splitId()).orElseThrow(()->new UserDefinedException("No split found with the ID"));
        if(!split.getOwner().getEmail().equalsIgnoreCase(me.getEmail())){
            throw new UserDefinedException("You cannot mark as received.");
        }
        
        
        EachUserPayment paymentOfUser=split.getEachUserPayments().stream().filter(e->e.getUser().getUsername().equalsIgnoreCase(user.getUsername())).findFirst().orElse(null);
        if(paymentOfUser==null){
            throw new UserDefinedException("There is no person with username:"+paymentRecord.personsUsername()+" in the split.");
        }
        if (paymentOfUser.getEachShareAmount().compareTo(paymentRecord.amount()) != 0) {
            throw new UserDefinedException("Amount doesn't match");
        }
        if(paymentOfUser.getPaymentStatus().equals(EachPaymentStatus.PENDING)){
            paymentOfUser.setPaymentStatus(EachPaymentStatus.PAID);
        }
        else{
            throw new UserDefinedException("Unable to reject payment that\'s on status: "+paymentOfUser.getPaymentStatus().name());
        }
        if(!paymentRecord.isThereATransaction()){
            Transactions transaction=new Transactions();
            Long accountidO = accountRepository
                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                Integer.parseInt(paymentRecord.maskedAccountno().substring(paymentRecord.maskedAccountno().length() - 4)), me.getEmail())
                                .orElseThrow(() -> new UserDefinedException(
                                                "No account found with accountid " + paymentRecord.maskedAccountno() + " for user "
                                                                + me.getEmail()));
            Account account=accountRepository.findById(accountidO).orElseThrow(() -> new UserDefinedException("No account found with number"));
            account.setBalance(account.getBalance().add(paymentRecord.amount()));
            transaction.setAmount(paymentRecord.amount());
            transaction.setCategory(TransactionCategory.SPLIT);
            transaction.setDescription("[Split Record] "+paymentOfUser.getUser().getName()+" has paid on the split.");
            transaction.setFromAccountno(account);
            transaction.setTransactiontime(LocalDate.now());
            transaction.setType("CREDIT");
            split.addTransaction(transaction);
        }
        return "Marked as Received.";
    }

    @Transactional 
    @Override
    public String MarkAsPaid(PaymentRecord paymentRecord, String myEmail) {
        Users me=usersRepository.findByEmail(myEmail).orElseThrow(()->new UserDefinedException("No user exists with email: "+myEmail));
        Users user=usersRepository.findByUsernameIgnoreCase(paymentRecord.personsUsername()).orElseThrow(()->new UserDefinedException("No person exists with username: "+paymentRecord.personsUsername()));

        ExpenseSplit split=expenseSplitRepository.findById(paymentRecord.splitId()).orElseThrow(()->new UserDefinedException("No split found with the ID"));
        if(split.getOwner().getEmail().equalsIgnoreCase(me.getEmail())){
            throw new UserDefinedException("You cannot pay since you are the owner.");
        }
        EachUserPayment paymentOfUser=split.getEachUserPayments().stream().filter(e->e.getUser().getUsername().equalsIgnoreCase(user.getUsername())).findFirst().orElse(null);
        if(paymentOfUser==null){
            throw new UserDefinedException("There is no person with username:"+paymentRecord.personsUsername()+" in the split.");
        }
        if (paymentOfUser.getEachShareAmount().compareTo(paymentRecord.amount()) != 0) {
            throw new UserDefinedException("Amount doesn't match");
        }
        if(paymentOfUser.getPaymentStatus().equals(EachPaymentStatus.PENDING)||paymentOfUser.getPaymentStatus().equals(EachPaymentStatus.REJECTED)){
            paymentOfUser.setPaymentStatus(EachPaymentStatus.AWAITING_CONFIRMATION);
        }
        
        if(!paymentRecord.isThereATransaction()){
            Transactions transaction=new Transactions();
            Long accountidO = accountRepository
                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                Integer.parseInt(paymentRecord.maskedAccountno().substring(paymentRecord.maskedAccountno().length() - 4)), me.getEmail())
                                .orElseThrow(() -> new UserDefinedException(
                                                "No account found with accountid " + paymentRecord.maskedAccountno() + " for user "
                                                                + me.getEmail()));
            Account account=accountRepository.findById(accountidO).orElseThrow(() -> new UserDefinedException("No account found with number"));
            account.setBalance(account.getBalance().subtract(paymentRecord.amount()));
            transaction.setAmount(paymentRecord.amount());
            transaction.setCategory(TransactionCategory.SPLIT);
            transaction.setDescription("[Split Record] "+"Paid on the split.");
            transaction.setFromAccountno(account);
            transaction.setTransactiontime(LocalDate.now());
            transaction.setType("DEBIT");
            paymentOfUser.setTransaction(transaction);
            transactionRepository.save(transaction);
        }
        return "Paid successfully.";
    }

}
