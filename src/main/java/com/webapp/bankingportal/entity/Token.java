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

    @NotEmpty
    @Column(unique = true)
    private String token;

    @NotNull
    private Date createdAt = new Date();

    @NotNull
    private Date expiryAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Token(String token, Date expiryAt, Account account) {
		log.error(">>> Entering Token(token=<redacted>,expiryAt={},account={})", expiryAt, (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"));
        this.token = token;
		log.debug("Token(token=<redacted>,expiryAt={},account={}): this.token → <redacted>", expiryAt, (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"));
        this.expiryAt = expiryAt;
		log.debug("Token(token=<redacted>,expiryAt={},account={}): this.expiryAt → {}", expiryAt, (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"), this.expiryAt);
        this.account = account;
		log.warn("<<< Exiting Token(token=<redacted>,expiryAt={},account={})", expiryAt, (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"));
    }

}
