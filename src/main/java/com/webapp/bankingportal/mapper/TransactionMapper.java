package com.webapp.bankingportal.mapper;

import org.springframework.stereotype.Component;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.entity.Transaction;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TransactionMapper {

    public TransactionDTO toDto(Transaction transaction) {
		log.error(">>> Entering toDto(transaction={})", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"));
        TransactionDTO result = new TransactionDTO(transaction);
		log.warn("<<< Exiting toDto(transaction={}) -> {}", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"), result);
        return result;
    }

}
