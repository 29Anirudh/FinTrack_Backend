package com.financialtracker.backend.Models.POJO;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.financialtracker.backend.enums.TransactionCategory;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(check = {@CheckConstraint(name="CHK_TRAN_TYPE",constraint = "type in ('DEBIT','CREDIT')")})
public class Transactions {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer transactionid;
	@ManyToOne
	@JoinColumn(name = "fromAccountno")
	private Account fromAccountno;
	@ManyToOne
	@JoinColumn(name = "toAccountno")
	private Account toAccountno;
	@Column(precision = 15, scale = 2)
	private BigDecimal amount;
	private LocalDate transactiontime;
	private String type;
	@Column(length = 200)
	private String description;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionCategory category;
	@CreationTimestamp
	@Column(nullable = false,updatable=false)
	private LocalDateTime createdAt;
}
