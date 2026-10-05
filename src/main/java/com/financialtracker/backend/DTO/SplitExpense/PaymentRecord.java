package com.financialtracker.backend.DTO.SplitExpense;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRecord(String personsUsername,BigDecimal amount,UUID splitId,Boolean isThereATransaction,String maskedAccountno) {
} 