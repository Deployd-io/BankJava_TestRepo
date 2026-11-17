package com.webapp.bankingportal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@NoArgsConstructor
@Data
public class OtpInfo {
private static final Logger log = LoggerFactory.getLogger(OtpInfo.class);

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
		log.error(">>> Entering OtpInfo(accountNumber={},otp=<redacted>,generatedAt={})", accountNumber, generatedAt);
		log.debug("OtpInfo(accountNumber={},otp=<redacted>,generatedAt={}): otp → <redacted>", accountNumber, generatedAt);
        this.accountNumber = accountNumber;
        this.otp = otp;
		log.debug("OtpInfo(accountNumber={},otp=<redacted>,generatedAt={}): this.accountNumber → {}", accountNumber, generatedAt, this.accountNumber);
        this.generatedAt = generatedAt;
		log.debug("OtpInfo(accountNumber={},otp=<redacted>,generatedAt={}): this.otp → <redacted>", accountNumber, generatedAt);
		log.warn("<<< Exiting OtpInfo(accountNumber={},otp=<redacted>,generatedAt={})", accountNumber, generatedAt);
    }

}
