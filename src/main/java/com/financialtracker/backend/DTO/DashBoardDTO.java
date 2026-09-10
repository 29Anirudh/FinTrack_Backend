package com.financialtracker.backend.DTO;

import java.util.List;

import com.financialtracker.backend.DTO.DashboardDetails.OverviewDTO;

public record DashBoardDTO(OverviewDTO basic,List<UserAnalyticsDTO> debitsandcredits, List<MonthlyOverviewDTO> monthlyoverview,List<TransactionReturnDTO> topten) {

}
