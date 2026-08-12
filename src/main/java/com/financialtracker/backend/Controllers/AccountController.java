package com.financialtracker.backend.Controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financialtracker.backend.DTO.AccountDTO;
import com.financialtracker.backend.Models.BL.AccountBL;
import com.financialtracker.backend.Models.BL.TransactionBL;


@RestController
@RequestMapping("/account")
public class AccountController {
	Map<String,Object> resp=new HashMap<String, Object>();
	@Autowired
	AccountBL accountbl;
	@Autowired
	TransactionBL transactionbl;
	public String getUsername() {
		return SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getName();
	}
	@GetMapping("/fetchbyid/{accountno}")
	public ResponseEntity<?> fetchAccountById(@PathVariable("accountno") long accountno){
		resp.clear();
		resp.put("account", accountbl.getAccountById(accountno,getUsername()));
		return new ResponseEntity<>(resp,HttpStatus.OK);
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createAccount(@RequestBody AccountDTO acc) {
		resp.clear();
		String returnMsg=accountbl.createAccount(acc, getUsername());
		resp.put("msg", returnMsg);
		if(returnMsg.contains("already")){
			return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(resp,HttpStatus.CREATED);
	}
	
	@PostMapping("/resetpin/{accountno}")
	public ResponseEntity<?> resetPin(@PathVariable("accountno") long accountno,@RequestBody String pin){
		resp.clear();
		resp.put("msg", accountbl.resetPin(accountno, pin, getUsername()));
		return new ResponseEntity<>(resp,HttpStatus.OK);
	}
	
	@PostMapping("/authenticate/{accountno}")
	public ResponseEntity<?> authenticateAccount(@PathVariable("accountno") long accountno,@RequestBody String pin){
		resp.clear();
		resp.put("msg", accountbl.loginAccount(accountno, pin, getUsername()));
		return new ResponseEntity<>(resp,HttpStatus.OK);
	}

	@GetMapping("/accounts")
	public ResponseEntity<?> getAllAccountDetails(){
		resp.clear();
		resp.put("list", accountbl.getAllAccountDetails(getUsername()));
		return new ResponseEntity<>(resp,HttpStatus.OK);
	}
	@GetMapping("/basicdetails")
	public ResponseEntity<?> getBasicAccountDetailsOfUser(){
		resp.clear();
		resp.put("details", accountbl.getBasicAccountDetailsOfUser(getUsername()));
		return ResponseEntity.ok(resp);
	}
	
	@GetMapping("/dashboardinfo/{maskedaccountid}")
	public ResponseEntity<?> getAccountDetailsFromMaskedAccountid(@PathVariable("maskedaccountid") String maskedaccountid){
		resp.clear();
		resp.put("account", accountbl.getAccountByLast4DigitsOfAccountid(maskedaccountid, getUsername()));
		resp.put("transactions",transactionbl.getTopTransactions(maskedaccountid, getUsername()));
		return ResponseEntity.ok(resp);
	}

	@DeleteMapping("/delete/{maskedaccountid}")
	public ResponseEntity<?> deleteAccount(@PathVariable("maskedaccountid") String maskedaccountid){
		resp.clear();
		String returnMsg=accountbl.deleteAccount(maskedaccountid, getUsername());
		resp.put("msg",returnMsg);
		if(returnMsg.contains("No")){
			return new ResponseEntity<>(resp,HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(resp);
	}
}
