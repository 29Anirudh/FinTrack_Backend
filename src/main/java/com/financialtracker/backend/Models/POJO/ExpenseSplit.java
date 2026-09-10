package com.financialtracker.backend.Models.POJO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.financialtracker.backend.enums.SplitMode;
import com.financialtracker.backend.enums.SplitStatus;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor 
@NoArgsConstructor 
@Data 

@Entity 
public class ExpenseSplit {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100)
    private String description;

    private Integer countOfMembers;

    @OneToMany(mappedBy = "each_user_expense",cascade = CascadeType.ALL,orphanRemoval = true)
    List<EachUserPayment> eachUserPayments=new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private SplitMode splitMode;

    private LocalDate dateOfExpense;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private SplitStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "owner_id")
    private Users owner;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn (name = "created_by_id")
    private Users createdBy;

    @CreationTimestamp 
    @Column (nullable = false,updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp 
    private LocalDateTime updatedAt;

}
