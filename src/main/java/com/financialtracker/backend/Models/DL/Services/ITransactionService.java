package com.financialtracker.backend.Models.DL.Services;

import java.util.List;

import com.financialtracker.backend.DTO.DashBoardDTO;
import com.financialtracker.backend.DTO.TotalOverviewOfUser;
import com.financialtracker.backend.DTO.TransactionDetails;
import com.financialtracker.backend.DTO.TransactionInputDTO;
import com.financialtracker.backend.DTO.TransactionReturnDTO;

public interface ITransactionService {
	List<TransactionReturnDTO> getAllTransactions(String username);
	List<TransactionReturnDTO> getAllTransactionsByAccountno(String username,String accountno);
	TransactionReturnDTO getTransactionById(String username,String maskedaccountno,int transactionid);
	String insertTransaction(TransactionInputDTO transaction,String username,String accountno);
	String updateTransaction(TransactionInputDTO transaction,String username,String accountno);
	String deleteTransaction(int transactionid,String username,String accountid);
	TransactionDetails getInformationFromMessage(String message,String username);
	DashBoardDTO getDashboardDetails(String username);
	TotalOverviewOfUser getDebitAndCreditOverview(String username);
	List<TransactionReturnDTO> getTopTransactions(String accountid,String username);
}
