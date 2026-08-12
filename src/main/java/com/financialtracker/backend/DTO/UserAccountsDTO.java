package com.financialtracker.backend.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserAccountsDTO(String accountno,BigDecimal balance,String bankname,String accountType,String status,LocalDateTime createdAt) {
    
}
