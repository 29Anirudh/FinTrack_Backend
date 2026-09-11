package com.financialtracker.backend.Models.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financialtracker.backend.Models.POJO.EachUserPayment;

public interface EachUserPaymentRepository extends JpaRepository<EachUserPayment,Long>{

}
