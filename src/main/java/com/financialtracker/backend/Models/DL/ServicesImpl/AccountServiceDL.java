package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financialtracker.backend.DTO.AccountDTO;
import com.financialtracker.backend.DTO.AccountNameandNumberDTO;
import com.financialtracker.backend.DTO.AccountReturnDTO;
import com.financialtracker.backend.DTO.UserAccountsDTO;
import com.financialtracker.backend.Exceptions.UserDefinedException;
import com.financialtracker.backend.Models.DL.Services.IAccountServiceDL;
import com.financialtracker.backend.Models.POJO.Account;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.AccountRepository;
import com.financialtracker.backend.Models.Repositories.TransactionRepository;
import com.financialtracker.backend.Models.Repositories.UsersRepository;

@Service
public class AccountServiceDL implements IAccountServiceDL {
	@Autowired
	AccountRepository accountrepository;
	@Autowired
	UsersRepository usersrepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	TransactionRepository transactionRepository;

	public String MaskAccountId(long accountid) {
		String accountidMasked = String.valueOf(accountid);
		return "X".repeat(accountidMasked.length() - 4) + accountidMasked.substring(accountidMasked.length() - 4);
	}

	@Override
	public String createAccount(AccountDTO acc, String username) {
		Optional<Account> accold = accountrepository.findByAccountnoAndUserEmail(acc.getAccountno(), username);
		if (accold.isEmpty()) {
			Users user = usersrepository.findByEmail(username)
					.orElseThrow(() -> new UserDefinedException("No user  found with email `" + username + "`"));
			Account accnew = new Account();
			accnew.setAccountno(acc.getAccountno());
			accnew.setAccountType(acc.getAccountType());
			accnew.setBalance(acc.getBalance());
			accnew.setBankname(acc.getBankname());
			accnew.setPin(passwordEncoder.encode(acc.getPin()));
			accnew.setStatus("Active");
			accnew.setUser(user);
			accountrepository.save(accnew);
			return acc.getAccountType() + " account with Account No: " + acc.getAccountno() + " saved succesfully";
		} else {
			return acc.getAccountno() + " already exists for some user.Please check your account number";
		}

	}
	@Transactional
	@Override
	public String deleteAccount(String maskedaccountid, String username) {

		int lastFourDigits = Integer.parseInt(
				maskedaccountid.substring(maskedaccountid.length() - 4));

		Long id = accountrepository
				.findByAccountnoLastFourDigitsAndUserEmail(lastFourDigits, username)
				.orElseThrow(() -> new UserDefinedException(
						"No account found with accountid "
								+ maskedaccountid
								+ " for user "
								+ username));
		Account accold=accountrepository.findById(id).orElseThrow(()->new UserDefinedException(""));
		
		transactionRepository.deleteByFromAccountno(accold);
		accountrepository.deleteById(id);

		return "Account with number "
				+ maskedaccountid
				+ " deleted successfully";
	}

	@Override
	public AccountReturnDTO fetchByAccountId(long id, String username) {
		Account accold = accountrepository.findByAccountnoAndUserEmail(id, username).orElseThrow(
				() -> new UserDefinedException("Account `" + id + "` not found for the user `" + username + "`"));
		return new AccountReturnDTO(accold.getAccountno(), accold.getBalance(), accold.getAccountType(),
				accold.getBankname(), accold.getStatus(), accold.getCreatedAt(), accold.getUpdatedAt());
	}

	@Override
	public String resetPin(long id, String pin, String username) {
		Account accold = accountrepository.findByAccountnoAndUserEmail(id, username).orElseThrow(
				() -> new UserDefinedException("Account `" + id + "` not found for the user `" + username + "`"));
		accold.setPin(passwordEncoder.encode(pin));
		accountrepository.save(accold);
		return "PIN reset successful";
	}

	@Override
	public Boolean loginAccount(long id, String pin, String username) {
		Account accold = accountrepository.findByAccountnoAndUserEmail(id, username).orElseThrow(
				() -> new UserDefinedException("Account `" + id + "` not found for the user `" + username + "`"));
		return passwordEncoder.matches(pin, accold.getPin());
	}

	@Override
	public List<UserAccountsDTO> getAllAccountsDetails(String username) {
		return accountrepository.getAllAccountsByUsername(username).stream()
				.map(a -> new UserAccountsDTO(MaskAccountId(a.accountno()), a.balance(), a.bankname(), a.accountType(),
						a.status(), a.createdAt()))
				.toList();
	}

	@Override
	public UserAccountsDTO getAccountDetails(String accountid, String username) {
		Long accountidO = accountrepository
				.findByAccountnoLastFourDigitsAndUserEmail(
						Integer.parseInt(accountid.substring(accountid.length() - 4)), username)
				.orElseThrow(() -> new UserDefinedException(
						"No account found with accountid " + accountid + " for user " + username));
		Account accold = accountrepository.findByAccountnoAndUserEmail(accountidO, username)
				.orElseThrow(() -> new UserDefinedException(
						"Account `" + accountidO + "` not found for the user `" + username + "`"));
		return new UserAccountsDTO(MaskAccountId(accold.getAccountno()), accold.getBalance(), accold.getBankname(),
				accold.getAccountType(), accold.getStatus(), accold.getCreatedAt());
	}

	@Override
	public List<AccountNameandNumberDTO> getBasicDetailsOfAccounts(String username) {
		return accountrepository.getAllAccountsByUsername(username).stream()
				.map(t -> new AccountNameandNumberDTO(t.bankname(), MaskAccountId(t.accountno()))).toList();
	}

}
