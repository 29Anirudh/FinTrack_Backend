package com.financialtracker.backend.Models.POJO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.financialtracker.backend.enums.EachPaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor 
@NoArgsConstructor 
@Data 

@Entity 
public class EachUserPayment {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_split_id",nullable = false)
    private ExpenseSplit expenseSplit;

    @Enumerated(EnumType.STRING)
    private EachPaymentStatus paymentStatus;

    private BigDecimal eachShareAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id",unique = true)
    private Transactions transaction;
    
    @CreationTimestamp 
    @Column (nullable = false,updatable = false)
    private LocalDateTime createdAt;
}
