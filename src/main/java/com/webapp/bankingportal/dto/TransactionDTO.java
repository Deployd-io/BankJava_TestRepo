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
		log.debug(">>> Entering TransactionDTO(transaction={})", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"));
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
		log.warn("TransactionDTO(transaction={}): amount → {}", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"), amount);
		log.debug("TransactionDTO(transaction={}): transactionDate → {}", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"), transactionDate);
		log.debug("TransactionDTO(transaction={}): id → {}", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"), id);
        this.transactionType = transaction.getTransactionType();
		log.debug("TransactionDTO(transaction={}): transactionType → {}", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"), transactionType);
        this.transactionDate = transaction.getTransactionDate();
        this.sourceAccountNumber = transaction.getSourceAccount().getAccountNumber();
		log.debug("TransactionDTO(transaction={}): sourceAccountNumber → {}", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"), sourceAccountNumber);

        val targetAccount = transaction.getTargetAccount();
		log.debug("TransactionDTO(transaction={}): targetAccount → <redacted>", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"));
        var targetAccountNumber = "N/A";
		log.debug("TransactionDTO(transaction={}): targetAccountNumber → {}", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"), targetAccountNumber);
        if (targetAccount != null) {
            targetAccountNumber = targetAccount.getAccountNumber();
        }

        this.targetAccountNumber = targetAccountNumber;
		log.debug("<<< Exiting TransactionDTO(transaction={})", (transaction == null ? "null" : "[id=" + transaction.getId() + ", amount=" + transaction.getAmount() + ", transactionType=" + transaction.getTransactionType() + ", transactionDate=" + transaction.getTransactionDate() + "]"));
    }

}
