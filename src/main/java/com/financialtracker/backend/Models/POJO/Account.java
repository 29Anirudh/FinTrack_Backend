package com.financialtracker.backend.Models.POJO;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(check = {
				@CheckConstraint(name="CHK_TYPE",constraint = "account_type in ('Savings','Current')")
		}
)
public class Account {
	@Id
	private Long accountno;
	@ManyToOne
	@JoinColumn(name = "userId")
	private Users user;
	private BigDecimal balance;
	@Column(length = 100)
	private String pin;
	@Column(length = 7)
	private String accountType;
	@Column(length = 30)
	private String bankname;
	@Column(length = 10)
	private String status;
	@CreationTimestamp
	@Column(nullable = false,updatable = false)
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
