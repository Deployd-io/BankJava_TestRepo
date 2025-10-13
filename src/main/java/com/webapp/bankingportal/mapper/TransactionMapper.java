package com.webapp.bankingportal.mapper;

import org.springframework.stereotype.Component;

import com.webapp.bankingportal.dto.TransactionDTO;
import lombok.extern.slf4j.Slf4j;
import com.webapp.bankingportal.entity.Transaction;

@Component
@Slf4j
public class TransactionMapper {

    public TransactionDTO toDto(Transaction transaction) {
		log.debug(">>> Entering toDto(transaction={})", transaction);
		log.debug("<<< Exiting toDto(transaction={})", transaction);
        return new TransactionDTO(transaction);
    }

}
