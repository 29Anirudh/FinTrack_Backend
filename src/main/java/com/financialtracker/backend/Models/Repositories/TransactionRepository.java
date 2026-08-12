package com.financialtracker.backend.Models.Repositories;

import java.util.List;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.financialtracker.backend.DTO.MonthlyOverviewDTO;
import com.financialtracker.backend.DTO.TotalOverviewOfUser;
import com.financialtracker.backend.DTO.TransactionQueryDTO;
import com.financialtracker.backend.DTO.UserAnalyticsDTO;
import com.financialtracker.backend.Models.POJO.Account;
import com.financialtracker.backend.Models.POJO.Transactions;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Integer> {
	@Query("select new com.financialtracker.backend.DTO.TransactionQueryDTO(t.transactionid,t.fromAccountno.accountno,t.fromAccountno.bankname,t.amount,t.type,t.category,t.description,t.transactiontime) from Transactions t where t.fromAccountno.accountno=?1")
	List<TransactionQueryDTO> getAllTransactionsByAccountno(long accountno);

	Optional<Transactions> findByTransactionidAndFromAccountnoAccountnoAndFromAccountnoUserEmail(int transactionid,long accountno, String username);

	@Query("""
			    SELECT new com.financialtracker.backend.DTO.TransactionQueryDTO(
			        t.transactionid,
			        t.fromAccountno.accountno,
			        t.fromAccountno.bankname,
			        t.amount,
			        t.type,
					t.category,
			        t.description,
			        t.transactiontime
			    )
			    FROM Transactions t
			    WHERE t.fromAccountno.accountno IN :accountnos
			    ORDER BY t.transactiontime DESC
			""")
	List<TransactionQueryDTO> getTopTransactionsByAccountnoBasedOnTransactionDate(
			@Param("accountnos") List<Long> accountnos,
			Pageable pageable);

	@Query("select new com.financialtracker.backend.DTO.UserAnalyticsDTO(t.type,sum(t.amount)) from Transactions t where t.fromAccountno.accountno IN :accountnos group by t.type")
	List<UserAnalyticsDTO> getDebitAndCreditBalanceEach(@Param("accountnos") List<Long> accountnos);

	@Query("SELECT new com.financialtracker.backend.DTO.MonthlyOverviewDTO("
			+ "    TRIM(TO_CHAR(t.createdAt, 'Month')),\r\n"
			+ "    SUM(CASE WHEN t.type = 'DEBIT' THEN t.amount ELSE 0 END),\r\n"
			+ "    SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END))\r\n"
			+ "FROM Transactions t WHERE t.fromAccountno.accountno IN ?1\r\n"
			+ "GROUP BY\r\n"
			+ "    TRIM(TO_CHAR(t.createdAt, 'Month')),\r\n"
			+ "    EXTRACT(MONTH FROM t.createdAt)\r\n"
			+ "ORDER BY\r\n"
			+ "    EXTRACT(MONTH FROM t.createdAt)")
	List<MonthlyOverviewDTO> getDebitAndCreditBalanceEachMonthForMultipleAccounts(List<Long> accountnos);

	@Query("select new com.financialtracker.backend.DTO.TransactionQueryDTO(t.transactionid,t.fromAccountno.accountno,t.fromAccountno.bankname,t.amount,t.type,t.category,t.description,t.transactiontime) from Transactions t where t.fromAccountno.user.email=?1 order by t.transactiontime")
	List<TransactionQueryDTO> getAllTransactionsByUsername(String username);

	void deleteByFromAccountno(Account account);

	@Query("""
			    SELECT new com.financialtracker.backend.DTO.TotalOverviewOfUser(
			        COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN t.type = 'DEBIT' THEN t.amount ELSE 0 END), 0),
			        COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END), 0)
			        -
			        COALESCE(SUM(CASE WHEN t.type = 'DEBIT' THEN t.amount ELSE 0 END), 0)
			    )
			    FROM Transactions t
			    WHERE t.fromAccountno.accountno IN :accountnos
			""")
	TotalOverviewOfUser getTotalOverviewOfUser(List<Long> accountnos);

}
