package com.financialtracker.backend.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserReturnDTO(int userid,List<Long> accountNumbers,String name,String email,LocalDate dateofbirth,String status,LocalDateTime createdAt) {

}
