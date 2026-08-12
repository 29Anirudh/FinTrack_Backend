package com.financialtracker.backend.Models.DL.Services;

import java.util.List;

import com.financialtracker.backend.DTO.AccountDTO;
import com.financialtracker.backend.DTO.AccountNameandNumberDTO;
import com.financialtracker.backend.DTO.AccountReturnDTO;
import com.financialtracker.backend.DTO.UserAccountsDTO;

public interface IAccountServiceDL {
	String createAccount(AccountDTO acc,String username);
	String deleteAccount(String id,String username);
	AccountReturnDTO fetchByAccountId(long id,String username);
	String resetPin(long id,String pin,String username);
	Boolean loginAccount(long id,String pin,String username);
	List<UserAccountsDTO> getAllAccountsDetails(String username);
	UserAccountsDTO getAccountDetails(String accountid,String username);
	List<AccountNameandNumberDTO> getBasicDetailsOfAccounts(String username);
}
