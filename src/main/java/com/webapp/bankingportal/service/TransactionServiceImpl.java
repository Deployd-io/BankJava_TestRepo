package com.webapp.bankingportal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.mapper.TransactionMapper;
import com.webapp.bankingportal.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

@Service
public static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
public static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionDTO> getAllTransactionsByAccountNumber(String accountNumber) {
        val transactions = transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber);

		log.info("getAllTransactionsByAccountNumber(accountNumber)={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", accountNumber, transactions);
        val transactionDTOs = transactions.parallelStream()
                .map(transactionMapper::toDto)
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .collect(Collectors.toList());
				log.info("getAllTransactionsByAccountNumber(accountNumber)={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", accountNumber, transactions);

		log.info("getAllTransactionsByAccountNumber(accountNumber)={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", accountNumber, transactions);
        return transactionDTOs;
    }

}
