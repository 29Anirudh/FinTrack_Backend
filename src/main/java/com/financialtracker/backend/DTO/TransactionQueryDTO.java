package com.financialtracker.backend.DTO;

import java.time.LocalDate;

import com.financialtracker.backend.enums.TransactionCategory;

public record TransactionQueryDTO(
    int transactionId,
    long accountno,
    String bankname,
    java.math.BigDecimal amount,
    String transactionType,
    TransactionCategory category,
    String description,
    LocalDate transactiontime
) {

}
