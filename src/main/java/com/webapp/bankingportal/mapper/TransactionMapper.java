package com.webapp.bankingportal.mapper;

import org.springframework.stereotype.Component;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TransactionMapper {
private static final Logger log = LoggerFactory.getLogger(TransactionMapper.class);

    public TransactionDTO toDto(Transaction transaction) {
		log.error(">>> Entering toDto(transaction={})", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"));
        TransactionDTO result = new TransactionDTO(transaction);
		log.warn("<<< Exiting toDto(transaction={}) -> {}", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"), result);
        return result;
    }

}
