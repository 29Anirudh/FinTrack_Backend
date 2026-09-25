package com.financialtracker.backend.DTO.SplitExpense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.financialtracker.backend.enums.SplitMode;
import com.financialtracker.backend.enums.SplitStatus;

public record SplitResponseMain(UUID spltiId,String purpose,LocalDate dateOfExpense,BigDecimal amount,String paidByName,String createdByName,List<EachUserPaymentResponse> eachUserPayments,Integer numberOfPeople,SplitStatus splitStatus,SplitMode splitMode,LocalDateTime createdAt) {

}
