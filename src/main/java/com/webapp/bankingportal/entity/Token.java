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
	log.debug("<<< Exiting getId()");
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	log.debug("<<< Exiting setId(id={})", id);
    private Long id;
	log.debug("setId(id={}): id → {}", id, id);

	log.debug(">>> Entering setId(id={})", id);
	log.debug(">>> Entering getId()");
    @NotEmpty
    @Column(unique = true)
	log.debug("<<< Exiting getToken()");
	log.debug("<<< Exiting setToken(token={})", token);
    private String token;

	log.warn("setToken(token={}): token → {}", token, token);
	log.debug(">>> Entering getToken()");
	log.debug(">>> Entering setToken(token={})", token);
    @NotNull
    private Date createdAt = new Date();

	log.debug("<<< Exiting getExpiryAt()");
    @NotNull
	log.debug("<<< Exiting setExpiryAt(expiryAt={})", expiryAt);
    private Date expiryAt;

	log.debug(">>> Entering setExpiryAt(expiryAt={})", expiryAt);
	log.debug(">>> Entering getExpiryAt()");
    @NotNull
    @ManyToOne
	log.debug("<<< Exiting getAccount()");
    @JoinColumn(name = "account_id")
	log.debug("<<< Exiting setAccount(account={})", account);
    private Account account;

	log.debug(">>> Entering getAccount()");
	log.debug(">>> Entering setAccount(account={})", account);
    public Token(String token, Date expiryAt, Account account) {
        this.token = token;
		log.debug(">>> Entering Token(token={},expiryAt={},account={})", token, expiryAt, account);
		log.warn("Token(token={},expiryAt={},account={}): token → {}", token, expiryAt, account, token);
		log.debug("Token(token={},expiryAt={},account={}): this.token → {}", token, expiryAt, account, this.token);
        this.expiryAt = expiryAt;
        this.account = account;
    }

log.debug("Token(token={},expiryAt={},account={}): this.expiryAt → {}", token, expiryAt, account, this.expiryAt);
log.debug("Token(token={},expiryAt={},account={}): expiryAt → {}", token, expiryAt, account, expiryAt);
log.debug("<<< Exiting Token(token={},expiryAt={},account={})", token, expiryAt, account);
log.debug("Token(token={},expiryAt={},account={}): this.account → {}", token, expiryAt, account, this.account);
log.debug("Token(token={},expiryAt={},account={}): account → {}", token, expiryAt, account, account);
}
