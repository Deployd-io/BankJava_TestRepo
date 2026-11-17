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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class TransferLimitValidator {
private static final Logger log = LoggerFactory.getLogger(TransferLimitValidator.class);

    private static final int MAX_REMARKS_LENGTH = 140;

    private final TransactionRepository transactionRepository;

    @Value("${banking.transfer.daily-limit:200000}")
    private double dailyLimit;

    public void validate(String sourceAccountNumber, double amount, String remarks) {
		log.error(">>> Entering validate(sourceAccountNumber={},amount={},remarks={})", sourceAccountNumber, amount, remarks);
        validateRemarks(remarks);

        double transferredToday = transferredOn(sourceAccountNumber, LocalDate.now());
		log.error("validate(sourceAccountNumber={},amount={},remarks={}): transferredToday → {}", sourceAccountNumber, amount, remarks, transferredToday);
        if (transferredToday + amount > dailyLimit) {
			log.error("Throwing FundTransferException from validate(sourceAccountNumber={},amount={},remarks={})", sourceAccountNumber, amount, remarks);
            throw new FundTransferException("Daily transfer limit of " + dailyLimit + " exceeded");
        }
		log.warn("<<< Exiting validate(sourceAccountNumber={},amount={},remarks={})", sourceAccountNumber, amount, remarks);
    }

    double transferredOn(String accountNumber, LocalDate day) {
		log.error(">>> Entering transferredOn(accountNumber={},day={})", accountNumber, day);
        double total = 0;
        for (Transaction transaction : transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber)) {
				log.error("transferredOn(accountNumber={},day={}): total → {}", accountNumber, day, total);
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
		log.warn("<<< Exiting transferredOn(accountNumber={},day={})", accountNumber, day);
        return total;
    }

    private void validateRemarks(String remarks) {
		log.error(">>> Entering validateRemarks(remarks={})", remarks);
        if (remarks == null) {
            return;
        }
        if (remarks.length() > MAX_REMARKS_LENGTH) {
			log.error("Throwing FundTransferException from validateRemarks(remarks={})", remarks);
            throw new FundTransferException("Remarks must be at most " + MAX_REMARKS_LENGTH + " characters");
        }
        if (remarks.chars().anyMatch(Character::isISOControl)) {
            throw new FundTransferException("Remarks must not contain control characters");
        }
		log.warn("<<< Exiting validateRemarks(remarks={})", remarks);
    }

    private static LocalDate toLocalDate(Date date) {
		log.error(">>> Entering toLocalDate(date={})", date);
        if (date == null) {
            return null;
        }
        LocalDate result = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		log.warn("<<< Exiting toLocalDate(date={}) -> {}", date, result);
        return result;
    }
}
