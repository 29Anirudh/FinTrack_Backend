package com.financialtracker.backend.DTO;

import java.math.BigDecimal;

public record TotalOverviewOfUser(BigDecimal credit_money,BigDecimal debit_money,BigDecimal net_money) {
}
