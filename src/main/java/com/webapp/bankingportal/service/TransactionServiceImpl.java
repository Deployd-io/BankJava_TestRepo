package com.webapp.bankingportal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.mapper.TransactionMapper;
import com.webapp.bankingportal.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionDTO> getAllTransactionsByAccountNumber(String accountNumber) {
		log.info(">>> Entering getAllTransactionsByAccountNumber(accountNumber={})", accountNumber);
        val transactions = transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber);

        val transactionDTOs = transactions.parallelStream()
                .map(transactionMapper::toDto)
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .collect(Collectors.toList());

        return transactionDTOs;
    }

}
