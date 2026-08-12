package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financialtracker.backend.DTO.DashBoardDTO;
import com.financialtracker.backend.DTO.MonthlyOverviewDTO;
import com.financialtracker.backend.DTO.OverviewDTO;
import com.financialtracker.backend.DTO.TotalOverviewOfUser;
import com.financialtracker.backend.DTO.TransactionDetails;
import com.financialtracker.backend.DTO.TransactionInputDTO;
import com.financialtracker.backend.DTO.TransactionQueryDTO;
import com.financialtracker.backend.DTO.TransactionReturnDTO;
import com.financialtracker.backend.DTO.UserAnalyticsDTO;
import com.financialtracker.backend.Exceptions.UserDefinedException;
import com.financialtracker.backend.Models.DL.Services.ITransactionService;
import com.financialtracker.backend.Models.POJO.Account;
import com.financialtracker.backend.Models.POJO.Transactions;
import com.financialtracker.backend.Models.POJO.Users;
import com.financialtracker.backend.Models.Repositories.AccountRepository;
import com.financialtracker.backend.Models.Repositories.TransactionRepository;
import com.financialtracker.backend.Models.Repositories.UsersRepository;
import com.financialtracker.backend.enums.TransactionCategory;

@Service
public class TransactionServiceDL implements ITransactionService {
        @Autowired
        UsersRepository userRepository;
        @Autowired
        AccountRepository accountRepository;
        @Autowired
        TransactionRepository transactionRepository;

        private Optional<Transactions> checkUsernameAndAccountnoAndTransationid(String username, long accountno,
                        int transactionid) {
                return transactionRepository.findByTransactionidAndFromAccountnoAccountnoAndFromAccountnoUserEmail(
                                transactionid, accountno, username);
        }

        private Optional<Account> checkUsernameAndAccountno(String username, long accountno) {
                return accountRepository.findByAccountnoAndUserEmail(accountno, username);
        }

        public String MaskAccountId(long accountid) {
                String accountidMasked = String.valueOf(accountid);
                return "X".repeat(accountidMasked.length() - 4)
                                + accountidMasked.substring(accountidMasked.length() - 4);
        }

        @Override
        public List<TransactionReturnDTO> getAllTransactionsByAccountno(String username, String accountno) {
                List<TransactionQueryDTO> list_main = new ArrayList<>();
                Long accountidO = accountRepository
                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                Integer.parseInt(accountno.substring(accountno.length() - 4)), username)
                                .orElseThrow(() -> new UserDefinedException(
                                                "No account found with accountid " + accountno + " for user "
                                                                + username));
                if (checkUsernameAndAccountno(username, accountidO).isPresent()) {
                        list_main = transactionRepository.getAllTransactionsByAccountno(accountidO);
                }
                return list_main.stream()
                                .map(t -> new TransactionReturnDTO(t.transactionId(), MaskAccountId(t.accountno()),
                                                t.bankname(), t.amount(), t.transactionType(),t.category().toString(),t.description(),
                                                t.transactiontime()))
                                .toList();
        }

        @Override
        public TransactionReturnDTO getTransactionById(String username, String Maskedaccountno, int transactionid) {
                Transactions tran_main = checkUsernameAndAccountnoAndTransationid(username,
                                accountRepository
                                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                                Integer.parseInt(Maskedaccountno.substring(
                                                                                Maskedaccountno.length() - 4)),
                                                                username)
                                                .get(),
                                transactionid)
                                .orElseThrow(() -> new UserDefinedException(
                                                "Some error occured with the transaction id or the account no."));
                return new TransactionReturnDTO(tran_main.getTransactionid(),
                                MaskAccountId(tran_main.getFromAccountno().getAccountno()),
                                tran_main.getFromAccountno().getBankname(),
                                tran_main.getAmount(), tran_main.getType(),tran_main.getCategory().toString(), tran_main.getDescription(),
                                tran_main.getTransactiontime());
        }

        @Override
        @Transactional
        public String insertTransaction(TransactionInputDTO transaction, String username, String accountno) {
                Account acc = checkUsernameAndAccountno(username,
                                accountRepository
                                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                                Integer.parseInt(accountno
                                                                                .substring(accountno.length() - 4)),
                                                                username)
                                                .orElseThrow(() -> new UserDefinedException(
                                                                "No account found with accountid " + accountno
                                                                                + " for user " + username)))
                                .orElseThrow(() -> new UserDefinedException(
                                                "User " + username + " doesn't have any account no :" + accountno));
                if (transaction.type().equals("DEBIT")) {
                        acc.setBalance(acc.getBalance().subtract(transaction.amount()));
                } else if (transaction.type().equals("CREDIT")) {
                        acc.setBalance(acc.getBalance().add(transaction.amount()));
                }
                Transactions tnew = new Transactions();
                tnew.setFromAccountno(acc);
                tnew.setAmount(transaction.amount());
                tnew.setDescription(transaction.description());
                tnew.setTransactiontime(transaction.transactiontime());
                tnew.setCategory(TransactionCategory.valueOf(transaction.category()));
                tnew.setType(transaction.type());
                transactionRepository.save(tnew);
                return "Saved the transaction successfully.";

        }

        @Override
        @Transactional
        public String updateTransaction(TransactionInputDTO transaction, String username, String accountno) {
                Transactions tran_main = checkUsernameAndAccountnoAndTransationid(username,
                                accountRepository
                                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                                Integer.parseInt(accountno
                                                                                .substring(accountno.length() - 4)),
                                                                username)
                                                .orElseThrow(() -> new UserDefinedException(
                                                                "No account found with accountid " + accountno
                                                                                + " for user " + username)),
                                transaction.transactionId()).orElseThrow(
                                                () -> new UserDefinedException(
                                                                "Some error occured with the transaction id or account no."));
                Account acc_update = tran_main.getFromAccountno();
                if (tran_main.getType().equals("DEBIT"))
                        acc_update.setBalance(acc_update.getBalance().add(tran_main.getAmount()));
                else
                        acc_update.setBalance(acc_update.getBalance().subtract(tran_main.getAmount()));

                tran_main.setDescription(transaction.description());
                tran_main.setTransactiontime(transaction.transactiontime());
                tran_main.setCategory(TransactionCategory.valueOf(transaction.category().toUpperCase()));
                tran_main.setAmount(transaction.amount());
                tran_main.setType(transaction.type());

                if (transaction.type().equals("DEBIT"))
                        acc_update.setBalance(acc_update.getBalance().subtract(transaction.amount()));
                else
                        acc_update.setBalance(acc_update.getBalance().add(transaction.amount()));
                return "Updated transaction succesfully.";
        }

        @Override
        @Transactional
        public String deleteTransaction(int transactionid, String username, String accountid) {
                Transactions tran_main = checkUsernameAndAccountnoAndTransationid(username,
                                accountRepository
                                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                                Integer.parseInt(accountid
                                                                                .substring(accountid.length() - 4)),
                                                                username)
                                                .orElseThrow(() -> new UserDefinedException(
                                                                "No account found with accountid " + accountid
                                                                                + " for user " + username)),
                                transactionid).orElseThrow(
                                                () -> new UserDefinedException(
                                                                "Some error occured with the transaction id or account no."));
                Account acc = tran_main.getFromAccountno();
                if (tran_main.getType().equals("DEBIT")) {
                        acc.setBalance(acc.getBalance().add(tran_main.getAmount()));
                } else if (tran_main.getType().equals("CREDIT"))
                        acc.setBalance(acc.getBalance().subtract(tran_main.getAmount()));
                transactionRepository.delete(tran_main);
                return "Deleted successfully";
        }

        @Override
        public TransactionDetails getInformationFromMessage(String message, String username) {

                if (message == null || message.isBlank()) {
                        throw new UserDefinedException("Transaction message cannot be empty");
                }

                // Amount
                Pattern moneyPattern = Pattern.compile("(INR|Rs\\.?)\\s?(\\d+(?:\\.\\d+)?)",
                                Pattern.CASE_INSENSITIVE);

                // Masked account number such as XXXX1234
                Pattern accountPattern = Pattern.compile("(X+)\\s?(\\d{4})",
                                Pattern.CASE_INSENSITIVE);
                // Dates supported:
                // 10-08-2026
                // 10-08-26
                // 10/08/26
                // 10/08/2026
                // 10-Aug-2026
                // 10-August-2026

                Pattern datePattern = Pattern.compile(
                                "\\b\\d{2}(?:[-/]\\d{2}[-/]\\d{2,4}|-(?:[A-Za-z]{3}|[A-Za-z]+)-\\d{2,4})\\b",
                                Pattern.CASE_INSENSITIVE);

                double money = 0;
                LocalDate date = null;
                long accountId;
                String type = null;

                // -----------------------------
                // Extract amount
                // -----------------------------
                Matcher moneyMatcher = moneyPattern.matcher(message);

                if (moneyMatcher.find()) {
                        money = Double.parseDouble(moneyMatcher.group(2));
                }

                // -----------------------------
                // Extract account number
                // -----------------------------
                Matcher accountMatcher = accountPattern.matcher(message);

                if (!accountMatcher.find()) {
                        throw new UserDefinedException(
                                        "Could not find a valid masked account number in the message");
                }

                int lastFourDigits = Integer.parseInt(accountMatcher.group(2));

                accountId = accountRepository
                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                lastFourDigits,
                                                username)
                                .orElseThrow(() -> new UserDefinedException(
                                                "No account found with account number ending with "
                                                                + lastFourDigits
                                                                + " for user "
                                                                + username));

                // -----------------------------
                // Extract date
                // -----------------------------
                Matcher dateMatcher = datePattern.matcher(message);

                if (dateMatcher.find()) {

                String dateText = dateMatcher.group();

                date = parseDate(dateText);

                if (date == null) {
                        throw new UserDefinedException(
                                "Invalid transaction date: " + dateText
                        );
                }
                }

                // -----------------------------
                // Extract transaction type
                // -----------------------------
                String lowerMessage = message.toLowerCase(Locale.ENGLISH);

                if (lowerMessage.contains("debit") || lowerMessage.contains("withdrawal")
                                || lowerMessage.contains("sent")) {
                        type = "DEBIT";
                } else if (lowerMessage.contains("credit") || lowerMessage.contains("received")
                                || lowerMessage.contains("deposit")) {
                        type = "CREDIT";
                }

                return new TransactionDetails(
                                MaskAccountId(accountId),
                                date,
                                money,
                                type);
        }

        @Override
        public DashBoardDTO getDashboardDetails(String username) {
                Users user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UserDefinedException(
                                                "User with email " + username + " doesn't exist"));
                BigDecimal balance = BigDecimal.ZERO;
                int count = 0;

                for (Account a : user.getAccounts()) {
                        balance = balance.add(a.getBalance());
                        count += transactionRepository.getAllTransactionsByAccountno(a.getAccountno()).size();
                }
                ;

                OverviewDTO o = new OverviewDTO(user.getAccounts().size(), balance, count);
                List<UserAnalyticsDTO> u = transactionRepository
                                .getDebitAndCreditBalanceEach(
                                                user.getAccounts().stream().map(a -> a.getAccountno()).toList());
                List<MonthlyOverviewDTO> m = transactionRepository.getDebitAndCreditBalanceEachMonthForMultipleAccounts(
                                user.getAccounts().stream().map(a -> a.getAccountno()).toList());
                List<TransactionQueryDTO> l = transactionRepository.getTopTransactionsByAccountnoBasedOnTransactionDate(
                                user.getAccounts().stream().map(a -> a.getAccountno()).toList(), Pageable.ofSize(10));
                return new DashBoardDTO(o, u, m,
                                l.stream()
                                                .map(t -> new TransactionReturnDTO(t.transactionId(),
                                                                MaskAccountId(t.accountno()),
                                                                t.bankname(), t.amount(), t.transactionType(),t.category().toString(),
                                                                t.description(), t.transactiontime()))
                                                .toList());
        }

        @Override
        public List<TransactionReturnDTO> getTopTransactions(String accountid, String username) {
                List<TransactionQueryDTO> lq = transactionRepository
                                .getTopTransactionsByAccountnoBasedOnTransactionDate(List.of(accountRepository
                                                .findByAccountnoLastFourDigitsAndUserEmail(
                                                                Integer.parseInt(accountid
                                                                                .substring(accountid.length() - 4)),
                                                                username)
                                                .orElseThrow(() -> new UserDefinedException(
                                                                "No account found with accountid " + accountid
                                                                                + " for user " + username))),
                                                Pageable.ofSize(5));
                return lq.stream().map(t -> new TransactionReturnDTO(t.transactionId(), MaskAccountId(t.accountno()),
                                t.bankname(), t.amount(), t.transactionType(), t.category().toString(),t.description(), t.transactiontime()))
                                .toList();
        }

        @Override
        public List<TransactionReturnDTO> getAllTransactions(String username) {
                List<TransactionQueryDTO> all_transactions = transactionRepository
                                .getAllTransactionsByUsername(username);
                return all_transactions.stream()
                                .map(t -> new TransactionReturnDTO(t.transactionId(), MaskAccountId(t.accountno()),
                                                t.bankname(),
                                                t.amount(), t.transactionType(),t.category().toString(), t.description(), t.transactiontime()))
                                .toList();
        }

        @Override
        public TotalOverviewOfUser getDebitAndCreditOverview(String username) {
                Users user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UserDefinedException(
                                                "User with email " + username + " doesn't exist"));
                List<Long> accountnos = user.getAccounts().stream().map(a -> a.getAccountno()).toList();
                return transactionRepository.getTotalOverviewOfUser(accountnos);
        }

        private LocalDate parseDate(String dateText) {

                // Numeric dates:
                // 10-08-2026
                // 10-08-26
                // 10/08/2026
                // 10/08/26
                if (dateText.matches("\\d{2}[-/]\\d{2}[-/]\\d{2,4}")) {

                        String[] parts = dateText.split("[-/]");

                        int day = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);
                        int year = Integer.parseInt(parts[2]);

                        // Convert 2-digit year to 4-digit year
                        if (parts[2].length() == 2) {
                                year += 2000;
                        }

                        try {
                                return LocalDate.of(year, month, day);
                        } catch (DateTimeException e) {
                                return null;
                        }
                }

                // Text month dates:
                // 10-Aug-2026
                // 10-Aug-2026
                // 10-August-2026
                if (dateText.matches("\\d{2}-[A-Za-z]+-\\d{2,4}")) {

                        String[] parts = dateText.split("-");

                        int day = Integer.parseInt(parts[0]);
                        String month = parts[1];
                        int year = Integer.parseInt(parts[2]);

                        if (parts[2].length() == 2) {
                                year += 2000;
                        }

                        String normalizedDate = String.format(
                                        Locale.ENGLISH,
                                        "%02d-%s-%04d",
                                        day,
                                        month,
                                        year);

                        DateTimeFormatter formatter;

                        if (month.length() == 3) {
                                formatter = DateTimeFormatter.ofPattern(
                                                "dd-MMM-uuuu",
                                                Locale.ENGLISH);
                        } else {
                                formatter = DateTimeFormatter.ofPattern(
                                                "dd-MMMM-uuuu",
                                                Locale.ENGLISH);
                        }

                        try {
                                return LocalDate.parse(normalizedDate, formatter);
                        } catch (DateTimeParseException e) {
                                return null;
                        }
                }

                return null;
        }

}
