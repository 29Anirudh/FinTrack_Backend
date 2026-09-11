package com.financialtracker.backend.DTO.SplitExpense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.financialtracker.backend.enums.SplitMode;

public record SplitRequest(String purpose,int numberOfPeople,List<EachUserPaymentRequest> eachUserPayments,SplitMode mode,LocalDate dateOfExpense,String ownerUsername,BigDecimal amount) {

}
