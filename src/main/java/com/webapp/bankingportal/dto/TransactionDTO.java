package com.webapp.bankingportal.dto;

import java.util.Date;

import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.entity.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
private static final Logger log = LoggerFactory.getLogger(TransactionDTO.class);

    private Long id;
    private double amount;
    private TransactionType transactionType;
    private Date transactionDate;
    private String sourceAccountNumber;
    private String targetAccountNumber;

    public TransactionDTO(Transaction transaction) {
		log.error(">>> Entering TransactionDTO(transaction={})", transaction);
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
		log.warn("TransactionDTO(transaction={}): amount → {}", transaction, amount);
		log.debug("TransactionDTO(transaction={}): id → {}", transaction, id);
        this.transactionType = transaction.getTransactionType();
		log.debug("TransactionDTO(transaction={}): transactionType → {}", transaction, transactionType);
        this.transactionDate = transaction.getTransactionDate();
		log.debug("TransactionDTO(transaction={}): transactionDate → {}", transaction, transactionDate);
        this.sourceAccountNumber = transaction.getSourceAccount().getAccountNumber();
		log.debug("TransactionDTO(transaction={}): sourceAccountNumber → {}", transaction, sourceAccountNumber);

        val targetAccount = transaction.getTargetAccount();
		log.error("TransactionDTO(transaction={}): targetAccount → <redacted>", transaction);
        var targetAccountNumber = "N/A";
		log.error("TransactionDTO(transaction={}): targetAccountNumber → {}", transaction, targetAccountNumber);
        if (targetAccount != null) {
            targetAccountNumber = targetAccount.getAccountNumber();
        }

        this.targetAccountNumber = targetAccountNumber;
		log.warn("<<< Exiting TransactionDTO(transaction={})", transaction);
		log.debug("TransactionDTO(transaction={}): this.targetAccountNumber → {}", transaction, this.targetAccountNumber);
    }

}
