package com.financialtracker.backend.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financialtracker.backend.DTO.TransactionInputDTO;
import com.financialtracker.backend.DTO.TransactionReturnDTO;
import com.financialtracker.backend.Models.BL.TransactionBL;
import com.financialtracker.backend.Models.Repositories.TransactionRepository;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    TransactionBL transactionBL;
    @Autowired
    TransactionRepository transactionrepository;
    Map<String,Object> resp=new HashMap<String,Object>();
    public String getUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
    @GetMapping("/all/{accountid}")
    public ResponseEntity<?> getAllTransactionsByAccountno(@PathVariable("accountid") String accountno){
        resp.clear();
        List<TransactionReturnDTO> list_all=transactionBL.getAllTransactionsByAccountno(getUsername(), accountno);
        if(!list_all.isEmpty()){
            resp.put("msg", "Fetch successful");
            resp.put("list", list_all);
            return new ResponseEntity<>(resp,HttpStatus.OK);
        }
        else{
            resp.put("msg", "No transactions found for the account no: "+accountno);
            return new ResponseEntity<>(resp,HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllTransactions(){
    	resp.clear();
        resp.put("overview", transactionBL.getDebitAndCreditOverview(getUsername()));
    	resp.put("list", transactionBL.getAllTransactionsByUsername(getUsername()));
    	return ResponseEntity.ok(resp);
    }
    
    @GetMapping("getbyid/acc/{accountid}/tran/{transactionid}")
    public ResponseEntity<?> getTransactionById(@PathVariable("accountid") String accountid,@PathVariable("transactionid") int transactionid){
        resp.clear();
        resp.put("transaction", transactionBL.getTransactionById(getUsername(), accountid, transactionid));
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }

    @PostMapping("/create/{accountid}")
    public ResponseEntity<?> createTransaction(@PathVariable("accountid") String accountno,@RequestBody TransactionInputDTO transaction){
        resp.clear();
        resp.put("msg", transactionBL.insertTransaction(transaction, getUsername(), accountno));
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }

    @DeleteMapping("/delete/acc/{accountid}/tran/{transactionid}")
     public ResponseEntity<?> deleteTransaction(@PathVariable("accountid") String accountid,@PathVariable("transactionid") int transactionid){
        resp.clear();
        resp.put("msg", transactionBL.deleteTransaction(transactionid, getUsername(), accountid));
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }

    @PutMapping("/update/{accountid}")
    public ResponseEntity<?> updateTransaction(@RequestBody TransactionInputDTO transaction,@PathVariable("accountid") String accountid){
        resp.clear();
        resp.put("msg", transactionBL.updateTransaction(transaction, getUsername(), accountid));
        return ResponseEntity.ok(resp);
    }
    
    @PostMapping("/getinfo")
    public ResponseEntity<?> getInformationFromMessage(@RequestBody String message){
    	resp.clear();
    	resp.put("data", transactionBL.getInformationFromMessage(message, getUsername()));
    	return ResponseEntity.ok(resp);
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getTopTenTransactionsByAccountNumber(){
    	resp.clear();
    	resp.put("list", transactionBL.getDashboardDetails(getUsername()));
    	return new ResponseEntity<>(resp,HttpStatus.OK);
    }
    
}
