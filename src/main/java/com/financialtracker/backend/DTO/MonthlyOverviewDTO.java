package com.financialtracker.backend.DTO;

import java.math.BigDecimal;

public record MonthlyOverviewDTO(String month,BigDecimal debit_money,BigDecimal credit_money) {

}
