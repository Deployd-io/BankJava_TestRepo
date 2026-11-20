package com.webapp.bankingportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StatementService {
private static final Logger log = LoggerFactory.getLogger(StatementService.class);

    private final TransactionRepository transactionRepository;

    public StatementService(TransactionRepository transactionRepository) {
		log.error(">>> Entering StatementService(transactionRepository={})", transactionRepository);
        this.transactionRepository = transactionRepository;
		log.warn("<<< Exiting StatementService(transactionRepository={})", transactionRepository);
    }

    public double totalIn(String accountNumber) {
		log.error(">>> Entering totalIn(accountNumber={})", accountNumber);
        List<Transaction> transactions = transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber);
        double total = 0;
		log.error("totalIn(accountNumber={}): total → {}", accountNumber, total);
        for (Transaction transaction : transactions) {
            if (transaction.getTargetAccount() != null
                    && accountNumber.equals(transaction.getTargetAccount().getAccountNumber())) {
                total += transaction.getAmount();
            }
        }
		log.warn("<<< Exiting totalIn(accountNumber={})", accountNumber);
        return total;
    }

    public int countTransactions(String accountNumber) {
		log.error(">>> Entering countTransactions(accountNumber={})", accountNumber);
        try {
            int result = transactionRepository
                    .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber)
                    .size();
			log.warn("<<< Exiting countTransactions(accountNumber={}) -> {}", accountNumber, result);
            return result;
        } catch (RuntimeException e) {
            throw new IllegalStateException("could not count transactions for " + accountNumber, e);
        }
    }
}
