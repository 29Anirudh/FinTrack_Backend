package com.financialtracker.backend.DTO.SplitExpense;

import java.math.BigDecimal;

public record EachUserPaymentRequest(String personUsername,BigDecimal eachShareAmount,Boolean isTransactionThere) {

}
