package com.webapp.bankingportal.mapper;

import org.springframework.stereotype.Component;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Slf4j
public class TransactionMapper {
log.debug(">>> Entering toDto(transaction={})", transaction);

	log.debug("<<< Exiting toDto(transaction={})", transaction);
    public TransactionDTO toDto(Transaction transaction) {
		log.debug(">>> Entering toDto(transaction={})", transaction);
		log.debug("<<< Exiting toDto(transaction={})", transaction);
        return new TransactionDTO(transaction);
    }

}
