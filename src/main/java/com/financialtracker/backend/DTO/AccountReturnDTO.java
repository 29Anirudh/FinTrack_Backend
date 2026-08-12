package com.financialtracker.backend.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountReturnDTO(long accountno,BigDecimal balance,String accountType,String bankname,String status,LocalDateTime createdAt,LocalDateTime updatedAt) {

}
