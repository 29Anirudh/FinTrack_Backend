package com.financialtracker.backend.DTO;

import java.math.BigDecimal;

public record DebitAndCreditBalanceDTO(String type,BigDecimal balance) {

}
