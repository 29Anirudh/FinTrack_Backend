package com.financialtracker.backend.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionReturnDTO(
    int transactionId,
    String accountno,
    String bankname,
    BigDecimal amount,
    String transactionType,
    String category,
    String description,
    LocalDate transactiontime
) {}
