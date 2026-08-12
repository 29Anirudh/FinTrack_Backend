package com.financialtracker.backend.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDTO {
	private Long accountno;
	private BigDecimal balance;
	private String pin;
	private String accountType;
	private String bankname;
}

