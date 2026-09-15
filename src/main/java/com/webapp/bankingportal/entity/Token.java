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
		log.info(">>> Entering Token(token=<redacted>,expiryAt={},account={})", expiryAt, account);
		log.error("Token(token=<redacted>,expiryAt={},account={}): expiryAt → {}", expiryAt, account, expiryAt);
        this.token = token;
        this.expiryAt = expiryAt;
		log.error("Token(token=<redacted>,expiryAt={},account={}): this.token → {}", expiryAt, account, this.token);
        this.account = account;
		log.info("<<< Exiting Token(token=<redacted>,expiryAt={},account={})", expiryAt, account);
    }

}
