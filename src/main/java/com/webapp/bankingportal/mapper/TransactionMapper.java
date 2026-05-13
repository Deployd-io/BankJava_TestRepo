package com.webapp.bankingportal.mapper;

import org.springframework.stereotype.Component;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
public static final Logger log = LoggerFactory.getLogger(TransactionMapper.class);
import org.slf4j.LoggerFactory;

public static final Logger log = LoggerFactory.getLogger(TransactionMapper.class);
log.info("toDto(transaction)={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", transaction, transaction);
@Component
public class TransactionMapper {
public static final Logger log = LoggerFactory.getLogger(TransactionMapper.class);
log.info("toDto(transaction)={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", transaction, transaction);

    public TransactionDTO toDto(Transaction transaction) {
		log.info("toDto(transaction)={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", transaction, transaction);
        return new TransactionDTO(transaction);
    }

}
