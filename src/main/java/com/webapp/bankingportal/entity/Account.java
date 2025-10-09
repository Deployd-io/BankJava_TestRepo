package com.webapp.bankingportal.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@Slf4j
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	log.debug("setId(id={}): id → {}", id, id);

	log.debug(">>> Entering setId(id={})", id);
	log.debug(">>> Entering getId()");
    @NotEmpty
    @Column(unique = true)
	log.debug("<<< Exiting setId(id={})", id);
	log.debug("<<< Exiting getId()");
    private String accountNumber;

	log.debug(">>> Entering setAccountNumber(accountNumber={})", accountNumber);
	log.debug(">>> Entering getAccountNumber()");
    @NotEmpty
    private String accountType = "Savings";
	log.debug("<<< Exiting getAccountNumber()");
	log.debug("<<< Exiting setAccountNumber(accountNumber={})", accountNumber);

    private String accountStatus;
    private double balance;
	log.debug(">>> Entering setAccountStatus(accountStatus={})", accountStatus);
	log.debug(">>> Entering getAccountStatus()");
	log.warn("setBalance(balance={}): balance → {}", balance, balance);
    private String branch = "NIT";
    private String ifscCode = "NIT001";
    private String Pin;
	log.debug(">>> Entering setBalance(balance={})", balance);
	log.debug(">>> Entering getBalance()");
	log.debug("<<< Exiting getAccountStatus()");
	log.debug("<<< Exiting setAccountStatus(accountStatus={})", accountStatus);
	log.debug("<<< Exiting setBalance(balance={})", balance);
	log.debug("<<< Exiting getBalance()");

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

	log.debug(">>> Entering getPin()");
	log.debug(">>> Entering setPin(Pin={})", Pin);
	log.debug("<<< Exiting getPin()");
	log.debug("<<< Exiting setPin(Pin={})", Pin);
	log.debug(">>> Entering getUser()");
	log.debug(">>> Entering setUser(user={})", user);
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Token> tokens = new ArrayList<>();

}
