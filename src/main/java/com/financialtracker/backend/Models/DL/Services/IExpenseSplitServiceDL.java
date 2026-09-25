package com.financialtracker.backend.Models.DL.Services;

import java.util.List;
import java.util.UUID;

import com.financialtracker.backend.DTO.SplitExpense.SplitRequest;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseBasic;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseMain;

public interface IExpenseSplitServiceDL {
    String createExpenseSplit(SplitRequest splitRequest,String createdByUsername);

    List<SplitResponseBasic> getBasicSplits(String myEmail);
    SplitResponseMain getExpenseDetail(String myEmail,UUID expenseId );
}
