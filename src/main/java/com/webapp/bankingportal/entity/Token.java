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
		log.debug(">>> Entering Token(token=<redacted>,expiryAt={},account={})", expiryAt, "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]");
		log.debug("Token(token=<redacted>,expiryAt={},account={}): expiryAt → {}", expiryAt, "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]", expiryAt);
        this.token = token;
		log.debug("Token(token=<redacted>,expiryAt={},account={}): this.token → <redacted>", expiryAt, "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]");
        this.expiryAt = expiryAt;
        this.account = account;
		log.debug("<<< Exiting Token(token=<redacted>,expiryAt={},account={})", expiryAt, "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]");
    }

}
