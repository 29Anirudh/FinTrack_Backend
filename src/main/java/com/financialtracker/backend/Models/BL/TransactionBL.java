package com.financialtracker.backend.Models.BL;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.financialtracker.backend.DTO.DashBoardDTO;
import com.financialtracker.backend.DTO.TotalOverviewOfUser;
import com.financialtracker.backend.DTO.TransactionDetails;
import com.financialtracker.backend.DTO.TransactionInputDTO;
import com.financialtracker.backend.DTO.TransactionReturnDTO;
import com.financialtracker.backend.Models.DL.ServicesImpl.TransactionServiceDL;

public class TransactionBL {
    @Autowired
    TransactionServiceDL transactionServiceDL;
    public List<TransactionReturnDTO> getAllTransactionsByAccountno(String username,String accountno){
        return transactionServiceDL.getAllTransactionsByAccountno(username, accountno);
    }
    public List<TransactionReturnDTO> getAllTransactionsByUsername(String username){
    	return transactionServiceDL.getAllTransactions(username);
    }
    public TransactionReturnDTO getTransactionById(String username, String accountno, int transactionid) {
        return transactionServiceDL.getTransactionById(username, accountno, transactionid);
    }
    public String insertTransaction(TransactionInputDTO transaction, String username, String accountno) {
        return transactionServiceDL.insertTransaction(transaction, username, accountno);
    }
    public String updateTransaction(TransactionInputDTO transaction, String username, String accountno) {
        return transactionServiceDL.updateTransaction(transaction, username, accountno);
    }
    public String deleteTransaction(int transactionid, String username, String accountid) {
        return transactionServiceDL.deleteTransaction(transactionid, username, accountid);
    }
    public TransactionDetails getInformationFromMessage(String message,String username) {
    	return transactionServiceDL.getInformationFromMessage(message, username);
    }
    public DashBoardDTO getDashboardDetails(String username) {
    	return transactionServiceDL.getDashboardDetails(username);
    }
    public List<TransactionReturnDTO> getTopTransactions(String accountid,String username){
    	return transactionServiceDL.getTopTransactions(accountid, username);
    }
    public TotalOverviewOfUser getDebitAndCreditOverview(String username) {
    	return transactionServiceDL.getDebitAndCreditOverview(username);
    }
    
}
