package com.financialtracker.backend.DTO.SplitExpense;

import java.math.BigDecimal;

import com.financialtracker.backend.enums.EachPaymentStatus;

public record EachUserPaymentResponse(Long id,String name,BigDecimal shareAmount,Boolean isOwner,EachPaymentStatus eachPaymentStatus) {
    
}
