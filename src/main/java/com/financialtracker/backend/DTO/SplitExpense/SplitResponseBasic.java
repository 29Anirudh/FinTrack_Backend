package com.financialtracker.backend.DTO.SplitExpense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.financialtracker.backend.enums.SplitStatus;

public record SplitResponseBasic(UUID spltiId,String purpose,LocalDate dateOfExpense,BigDecimal amount,BigDecimal myShare,String paidByName,Integer numberOfPeople,SplitStatus splitStatus,LocalDateTime createdAt) {

}
