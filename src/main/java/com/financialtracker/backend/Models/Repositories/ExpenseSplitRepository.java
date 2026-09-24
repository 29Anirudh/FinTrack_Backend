package com.financialtracker.backend.Models.Repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.financialtracker.backend.Models.POJO.ExpenseSplit;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit,UUID> {
    @Query ("select DISTINCT e from ExpenseSplit e LEFT JOIN e.eachUserPayments p WHERE e.owner.email=:email OR p.user.email=:email")
    List<ExpenseSplit> getAllRelatedExpenses(String email);
}
