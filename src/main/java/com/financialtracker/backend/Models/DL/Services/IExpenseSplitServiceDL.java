package com.financialtracker.backend.Models.DL.Services;

import java.util.List;
import java.util.UUID;

import com.financialtracker.backend.DTO.SplitExpense.BasicStatsOfSplits;
import com.financialtracker.backend.DTO.SplitExpense.PaymentRecord;
import com.financialtracker.backend.DTO.SplitExpense.SplitRequest;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseBasic;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseMain;

public interface IExpenseSplitServiceDL {
    String createExpenseSplit(SplitRequest splitRequest,String createdByUsername);

    List<SplitResponseBasic> getBasicSplits(String myEmail);
    SplitResponseMain getExpenseDetail(String myEmail,UUID expenseId );

    BasicStatsOfSplits getBasicStatsOfSplits(String myEmail);

    String confirmPayment(PaymentRecord paymentRecord,String myEmail);
    String rejectPayment(PaymentRecord paymentRecord,String myEmail);
    String MarkAsReceived(PaymentRecord paymentRecord,String myEmail);
    
    String MarkAsPaid(PaymentRecord paymentRecord,String myEmail);

}
