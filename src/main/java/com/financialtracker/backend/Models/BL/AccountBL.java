package com.financialtracker.backend.Models.BL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.financialtracker.backend.DTO.AccountDTO;
import com.financialtracker.backend.DTO.AccountNameandNumberDTO;
import com.financialtracker.backend.DTO.AccountReturnDTO;
import com.financialtracker.backend.DTO.UserAccountsDTO;
import com.financialtracker.backend.Models.DL.ServicesImpl.AccountServiceDL;

public class AccountBL {
	@Autowired
	AccountServiceDL accountservicedl;
	
	public AccountReturnDTO getAccountById(long id,String username) {
		return accountservicedl.fetchByAccountId(id,username);
	}
	
	public String createAccount(AccountDTO acc,String username) {
		return accountservicedl.createAccount(acc, username);
	}
	
	public String resetPin(long id,String pin,String username) {
		return accountservicedl.resetPin(id, pin, username);
	}
	public Boolean loginAccount(long id,String pin,String username) {
		return accountservicedl.loginAccount(id, pin, username);
	}
	public List<UserAccountsDTO> getAllAccountDetails(String username){
		return accountservicedl.getAllAccountsDetails(username);
	}
	public UserAccountsDTO getAccountByLast4DigitsOfAccountid(String id,String username) {
		return accountservicedl.getAccountDetails(id, username);
	}
	public List<AccountNameandNumberDTO> getBasicAccountDetailsOfUser(String username){
		return accountservicedl.getBasicDetailsOfAccounts(username);
	}
	public String deleteAccount(String maskedAccountId,String username){
		return accountservicedl.deleteAccount(maskedAccountId, username);
	}
}
