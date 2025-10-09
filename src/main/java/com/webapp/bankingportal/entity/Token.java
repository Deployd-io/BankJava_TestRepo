package com.webapp.bankingportal.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;
@Entity
@NoArgsConstructor
@Data
@Slf4j
public class Token {

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
    private String token;

	log.warn("setToken(token={}): token → {}", token, token);
	log.debug(">>> Entering getToken()");
	log.debug(">>> Entering setToken(token={})", token);
    @NotNull
    private Date createdAt = new Date();

	log.debug("<<< Exiting setToken(token={})", token);
	log.debug("<<< Exiting getToken()");
    @NotNull
    private Date expiryAt;

	log.debug(">>> Entering setExpiryAt(expiryAt={})", expiryAt);
	log.debug(">>> Entering getExpiryAt()");
    @NotNull
    @ManyToOne
	log.debug("<<< Exiting getExpiryAt()");
	log.debug("<<< Exiting setExpiryAt(expiryAt={})", expiryAt);
    @JoinColumn(name = "account_id")
    private Account account;

	log.debug(">>> Entering getAccount()");
	log.debug(">>> Entering setAccount(account={})", account);
    public Token(String token, Date expiryAt, Account account) {
        this.token = token;
		log.debug("<<< Exiting getAccount()");
		log.debug("<<< Exiting setAccount(account={})", account);
		log.debug(">>> Entering Token(token={},expiryAt={},account={})", token, expiryAt, account);
		log.warn("Token(token={},expiryAt={},account={}): token → {}", token, expiryAt, account, token);
		log.debug("Token(token={},expiryAt={},account={}): this.token → {}", token, expiryAt, account, this.token);
        this.expiryAt = expiryAt;
        this.account = account;
    }

}
