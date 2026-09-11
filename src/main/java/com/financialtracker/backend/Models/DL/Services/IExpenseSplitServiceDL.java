package com.financialtracker.backend.Models.DL.Services;

import com.financialtracker.backend.DTO.SplitExpense.SplitRequest;

public interface IExpenseSplitServiceDL {
    String createExpenseSplit(SplitRequest splitRequest,String createdByUsername);
}
