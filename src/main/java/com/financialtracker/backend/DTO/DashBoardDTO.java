package com.financialtracker.backend.DTO;

import java.util.List;

public record DashBoardDTO(OverviewDTO basic,List<UserAnalyticsDTO> debitsandcredits, List<MonthlyOverviewDTO> monthlyoverview,List<TransactionReturnDTO> topten) {

}
