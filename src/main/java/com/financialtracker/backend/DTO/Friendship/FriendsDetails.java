package com.financialtracker.backend.DTO.Friendship;

import java.math.BigDecimal;

public record FriendsDetails(Integer userId,String name,String username,String email,BigDecimal totalExpenseAmount) {

}
