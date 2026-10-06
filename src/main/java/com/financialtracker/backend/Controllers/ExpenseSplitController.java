package com.financialtracker.backend.Controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financialtracker.backend.DTO.SplitExpense.PaymentRecord;
import com.financialtracker.backend.DTO.SplitExpense.SplitRequest;
import com.financialtracker.backend.Models.BL.ExpenseSplitBL;

@RestController 
@RequestMapping("/split")
public class ExpenseSplitController {
    @Autowired 
    ExpenseSplitBL expenseSplitBL;

    private String getEmail(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSplit(@RequestBody SplitRequest splitRequest){
        Map<String,Object> response=new HashMap<>();
        response.put("msg", expenseSplitBL.createSplit(splitRequest, getEmail()));
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getAllExpenses(){
        Map<String,Object> response=new HashMap<>();
        response.put("statistics", expenseSplitBL.getBasicStatsOfSplits(getEmail()));
        response.put("expenses", expenseSplitBL.getBasicSplits(getEmail()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<?> getDashboard(){
        Map<String,Object> response=new HashMap<>();
        response.put("list", expenseSplitBL.getBasicSplits(getEmail()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<?> getExpenseDetails(@PathVariable("expenseId") UUID expenseId){
        Map<String,Object> response=new HashMap<>();
        response.put("details", expenseSplitBL.getExpenseInfo(getEmail(), expenseId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //On the owner side
    @PostMapping("/confirmpayment")
    public ResponseEntity<?> confirmPaymentInASplit(@RequestBody PaymentRecord paymentRecord){
        Map<String,Object> response=new HashMap<>();
        response.put("msg", expenseSplitBL.confirmPayment(paymentRecord, getEmail()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/rejectpayment")
    public ResponseEntity<?> rejectPaymentInASplit(@RequestBody PaymentRecord paymentRecord){
        Map<String,Object> response=new HashMap<>();
        response.put("msg", expenseSplitBL.rejectPayment(paymentRecord, getEmail()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/markasreceived")
    public ResponseEntity<?> markAsReceivedInASplit(@RequestBody PaymentRecord paymentRecord){
        Map<String,Object> response=new HashMap<>();
        response.put("msg", expenseSplitBL.markAsReceived(paymentRecord, getEmail()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //On the split payer side
    @PostMapping("/markaspaid")
    public ResponseEntity<?> markAsPaidInASplit(@RequestBody PaymentRecord paymentRecord){
        Map<String,Object> response=new HashMap<>();
        response.put("msg", expenseSplitBL.markAsPaid(paymentRecord, getEmail()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
