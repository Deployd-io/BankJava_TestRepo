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
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.transactionType = transaction.getTransactionType();
        this.transactionDate = transaction.getTransactionDate();
        this.sourceAccountNumber = transaction.getSourceAccount().getAccountNumber();

        val targetAccount = transaction.getTargetAccount();
        var targetAccountNumber = "N/A";
        if (targetAccount != null) {
            targetAccountNumber = targetAccount.getAccountNumber();
        }

        this.targetAccountNumber = targetAccountNumber;
		log.info("TransactionDTO(transaction)={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", transaction, transaction);
    }

}
