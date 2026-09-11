package com.financialtracker.backend.Models.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financialtracker.backend.Models.POJO.ExpenseSplit;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit,UUID> {

}
