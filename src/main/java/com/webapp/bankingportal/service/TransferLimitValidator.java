package com.webapp.bankingportal.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.entity.TransactionType;
import com.webapp.bankingportal.exception.FundTransferException;
import com.webapp.bankingportal.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferLimitValidator {

    private static final int MAX_REMARKS_LENGTH = 140;

    private final TransactionRepository transactionRepository;

    @Value("${banking.transfer.daily-limit:200000}")
    private double dailyLimit;

    public void validate(String sourceAccountNumber, double amount, String remarks) {
        validateRemarks(remarks);

        double transferredToday = transferredOn(sourceAccountNumber, LocalDate.now());
        if (transferredToday + amount > dailyLimit) {
            throw new FundTransferException("Daily transfer limit of " + dailyLimit + " exceeded");
        }
    }

    double transferredOn(String accountNumber, LocalDate day) {
        double total = 0;
        for (Transaction transaction : transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber)) {
            if (transaction.getTransactionType() != TransactionType.CASH_TRANSFER) {
                continue;
            }
            if (transaction.getSourceAccount() == null
                    || !accountNumber.equals(transaction.getSourceAccount().getAccountNumber())) {
                continue;
            }
            if (!day.equals(toLocalDate(transaction.getTransactionDate()))) {
                continue;
            }
            total += transaction.getAmount();
        }
        return total;
    }

    private void validateRemarks(String remarks) {
        if (remarks == null) {
            return;
        }
        if (remarks.length() > MAX_REMARKS_LENGTH) {
            throw new FundTransferException("Remarks must be at most " + MAX_REMARKS_LENGTH + " characters");
        }
        if (remarks.chars().anyMatch(Character::isISOControl)) {
            throw new FundTransferException("Remarks must not contain control characters");
        }
    }

    private static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
