package com.financialtracker.backend.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionInputDTO(Integer transactionId,String fromAccountno,BigDecimal amount,LocalDate transactiontime,String type,String category,String description) {

}
