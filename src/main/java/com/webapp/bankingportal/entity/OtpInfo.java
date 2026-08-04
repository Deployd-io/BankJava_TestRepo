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
public static final Logger log = LoggerFactory.getLogger(OtpInfo.class);

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
		log.debug(">>> Entering OtpInfo(accountNumber={},otp={},generatedAt={})", accountNumber, otp, generatedAt);
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): otp → {}", accountNumber, otp, generatedAt, otp);
        this.accountNumber = accountNumber;
        this.otp = otp;
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): this.accountNumber → {}", accountNumber, otp, generatedAt, this.accountNumber);
        this.generatedAt = generatedAt;
		log.debug("<<< Exiting OtpInfo(accountNumber={},otp={},generatedAt={})", accountNumber, otp, generatedAt);
    }

}
