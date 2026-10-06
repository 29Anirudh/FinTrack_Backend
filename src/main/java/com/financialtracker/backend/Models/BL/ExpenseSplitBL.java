package com.financialtracker.backend.Models.BL;


import com.financialtracker.backend.DTO.SplitExpense.BasicStatsOfSplits;
import com.financialtracker.backend.DTO.SplitExpense.PaymentRecord;
import com.financialtracker.backend.DTO.SplitExpense.SplitRequest;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseBasic;
import com.financialtracker.backend.DTO.SplitExpense.SplitResponseMain;
import com.financialtracker.backend.Models.DL.ServicesImpl.ExpenseSplitServiceDL;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

public class ExpenseSplitBL {

    @Autowired 
    ExpenseSplitServiceDL expenseSplitServiceDL;

    public String createSplit(SplitRequest splitRequest,String createdByEmail){
        return expenseSplitServiceDL.createExpenseSplit(splitRequest, createdByEmail);
    }

    public List<SplitResponseBasic> getBasicSplits(String myEmail){
        return expenseSplitServiceDL.getBasicSplits(myEmail);
    }

    public SplitResponseMain getExpenseInfo(String myEmail,UUID expenseId){
        return expenseSplitServiceDL.getExpenseDetail(myEmail, expenseId);
    }

    public BasicStatsOfSplits getBasicStatsOfSplits(String myEmail){
        return expenseSplitServiceDL.getBasicStatsOfSplits(myEmail);
    }

    public String confirmPayment(PaymentRecord paymentRecord,String myEmail){
        return expenseSplitServiceDL.confirmPayment(paymentRecord, myEmail);
    }

    public String rejectPayment(PaymentRecord paymentRecord,String myEmail){
        return expenseSplitServiceDL.rejectPayment(paymentRecord, myEmail);
    }

    public String markAsReceived(PaymentRecord paymentRecord,String myEmail){
        return expenseSplitServiceDL.MarkAsReceived(paymentRecord, myEmail);
    }
    
    public String markAsPaid(PaymentRecord paymentRecord,String myEmail){
        return expenseSplitServiceDL.MarkAsPaid(paymentRecord, myEmail);
    }
}
