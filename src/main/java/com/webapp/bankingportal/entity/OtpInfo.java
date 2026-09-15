package com.webapp.bankingportal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@NoArgsConstructor
@Data
@Slf4j
public class OtpInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String accountNumber;

    @Column
    private String otp;

    @Column
    private LocalDateTime generatedAt;

    public OtpInfo(String accountNumber, String otp, LocalDateTime generatedAt) {
		log.info(">>> Entering OtpInfo(accountNumber={},otp=<redacted>,generatedAt={})", accountNumber, generatedAt);
		log.error("OtpInfo(accountNumber={},otp=<redacted>,generatedAt={}): otp → {}", accountNumber, generatedAt, otp);
        this.accountNumber = accountNumber;
        this.otp = otp;
		log.error("OtpInfo(accountNumber={},otp=<redacted>,generatedAt={}): this.accountNumber → {}", accountNumber, generatedAt, this.accountNumber);
        this.generatedAt = generatedAt;
		log.info("<<< Exiting OtpInfo(accountNumber={},otp=<redacted>,generatedAt={})", accountNumber, generatedAt);
    }

}
