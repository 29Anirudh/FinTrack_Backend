package com.financialtracker.backend.Models.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.financialtracker.backend.DTO.AccountReturnDTO;
import com.financialtracker.backend.Models.POJO.Account;
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByAccountnoAndUserEmail(long accountno,String username);
	@Query("select A.accountno from Account A where MOD(A.accountno,10000)=?1 and A.user.email=?2")
	Optional<Long> findByAccountnoLastFourDigitsAndUserEmail(int accountnoLastFourdigits,String username);
	@Query("select new com.financialtracker.backend.DTO.AccountReturnDTO(A.accountno,A.balance,A.accountType,A.bankname,A.status,A.createdAt,A.updatedAt) from Account A where A.user.email=?1")
	List<AccountReturnDTO> getAllAccountsByUsername(String username);
}
