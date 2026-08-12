package com.financialtracker.backend.DTO;

import java.time.LocalDate;

public record TransactionDetails(String accountno,LocalDate transactiondate,double amount,String type) {
    
}
