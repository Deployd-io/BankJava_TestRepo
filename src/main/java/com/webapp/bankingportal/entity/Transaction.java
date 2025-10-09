package com.webapp.bankingportal.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@Slf4j
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	log.debug("setId(id={}): id → {}", id, id);
    private double amount;
	log.debug(">>> Entering setId(id={})", id);
	log.debug(">>> Entering getId()");
	log.warn("setAmount(amount={}): amount → {}", amount, amount);

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
	log.debug(">>> Entering setAmount(amount={})", amount);
	log.debug(">>> Entering getAmount()");
	log.debug("<<< Exiting setId(id={})", id);
	log.debug("<<< Exiting getId()");
	log.debug("<<< Exiting getAmount()");
	log.debug("<<< Exiting setAmount(amount={})", amount);

    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;
	log.debug(">>> Entering setTransactionType(transactionType={})", transactionType);
	log.debug(">>> Entering getTransactionType()");
	log.debug("<<< Exiting setTransactionType(transactionType={})", transactionType);
	log.debug("<<< Exiting getTransactionType()");
	log.debug(">>> Entering getTransactionDate()");
	log.debug(">>> Entering setTransactionDate(transactionDate={})", transactionDate);
	log.debug("<<< Exiting setTransactionDate(transactionDate={})", transactionDate);
	log.debug("<<< Exiting getTransactionDate()");

    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

}
