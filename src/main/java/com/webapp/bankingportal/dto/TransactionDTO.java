package com.webapp.bankingportal.dto;

import java.util.Date;

import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.entity.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TransactionDTO {

    private Long id;
    private double amount;
    private TransactionType transactionType;
    private Date transactionDate;
    private String sourceAccountNumber;
    private String targetAccountNumber;

    public TransactionDTO(Transaction transaction) {
		log.info(">>> Entering TransactionDTO(transaction={})", transaction);
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
		log.error("TransactionDTO(transaction={}): amount → {}", transaction, amount);
		log.error("TransactionDTO(transaction={}): id → {}", transaction, id);
        this.transactionType = transaction.getTransactionType();
		log.error("TransactionDTO(transaction={}): transactionType → {}", transaction, transactionType);
        this.transactionDate = transaction.getTransactionDate();
		log.error("TransactionDTO(transaction={}): transactionDate → {}", transaction, transactionDate);
        this.sourceAccountNumber = transaction.getSourceAccount().getAccountNumber();
		log.error("TransactionDTO(transaction={}): sourceAccountNumber → {}", transaction, sourceAccountNumber);

        val targetAccount = transaction.getTargetAccount();
		log.error("TransactionDTO(transaction={}): targetAccount → {}", transaction, targetAccount);
        var targetAccountNumber = "N/A";
		log.error("TransactionDTO(transaction={}): targetAccountNumber → {}", transaction, targetAccountNumber);
        if (targetAccount != null) {
            targetAccountNumber = targetAccount.getAccountNumber();
        }

        this.targetAccountNumber = targetAccountNumber;
		log.info("<<< Exiting TransactionDTO(transaction={})", transaction);
    }

}
