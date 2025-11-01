package com.webapp.bankingportal.mapper;

import org.springframework.stereotype.Component;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.entity.Transaction;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TransactionMapper {

    public TransactionDTO toDto(Transaction transaction) {
		log.info("toDto(transaction)={}): SOC2-AUDIT: Database transaction completed with status {}", transaction, transactionStatus);
        return new TransactionDTO(transaction);
    }

}
