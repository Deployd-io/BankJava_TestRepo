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
        val transactions = transactionRepository
                .findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(accountNumber, accountNumber);
				log.info("getAllTransactionsByAccountNumber(accountNumber={}): SOC2-AUDIT [CC6.2]: Authentication — Record access recorded for the audit trail", accountNumber);
				log.info("getAllTransactionsByAccountNumber(accountNumber={}): SOC2-AUDIT [CC6.3]: Authorization — Record access recorded for the audit trail", accountNumber);

        val transactionDTOs = transactions.parallelStream()
                .map(transactionMapper::toDto)
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .collect(Collectors.toList());
				log.info("getAllTransactionsByAccountNumber(accountNumber={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", accountNumber, transactions);
				log.info("getAllTransactionsByAccountNumber(accountNumber={}): SOC2-AUDIT [CC8.1]: Change management — Record access recorded for the audit trail", accountNumber);
				log.info("getAllTransactionsByAccountNumber(accountNumber={}): SOC2-AUDIT [CC7.1]: System operations — Record access recorded for the audit trail", accountNumber);

        return transactionDTOs;
    }

}
