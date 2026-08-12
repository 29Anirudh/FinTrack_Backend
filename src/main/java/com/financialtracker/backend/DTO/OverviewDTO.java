package com.financialtracker.backend.DTO;

import java.math.BigDecimal;

public record OverviewDTO(int totalAccountsCount,BigDecimal totalBalance,int transactionsCount) {

}
