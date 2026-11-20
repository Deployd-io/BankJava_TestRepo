package com.webapp.bankingportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.repository.TransactionRepository;

@Service
public class StatementService {

    private final TransactionRepository transactionRepository;

    public StatementService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public double totalIn(String accountNumber) {
        List<Transaction> transactions = transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber);
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getTargetAccount() != null
                    && accountNumber.equals(transaction.getTargetAccount().getAccountNumber())) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    public int countTransactions(String accountNumber) {
        try {
            return transactionRepository
                    .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber)
                    .size();
        } catch (RuntimeException e) {
            throw new IllegalStateException("could not count transactions for " + accountNumber, e);
        }
    }
}
