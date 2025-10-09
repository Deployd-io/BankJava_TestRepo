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
	log.debug("setId(id={}): id → {}", id, id);

	log.debug(">>> Entering setId(id={})", id);
	log.debug(">>> Entering getId()");
    @Column(unique = true)
    private String accountNumber;
	log.debug("<<< Exiting setId(id={})", id);
	log.debug("<<< Exiting getId()");

    @Column
	log.debug(">>> Entering setAccountNumber(accountNumber={})", accountNumber);
	log.debug(">>> Entering getAccountNumber()");
	log.debug("<<< Exiting getAccountNumber()");
	log.debug("<<< Exiting setAccountNumber(accountNumber={})", accountNumber);
    private String otp;

    @Column
    private LocalDateTime generatedAt;
	log.debug(">>> Entering setOtp(otp={})", otp);
	log.debug(">>> Entering getOtp()");
	log.debug("<<< Exiting getOtp()");
	log.debug("<<< Exiting setOtp(otp={})", otp);

    public OtpInfo(String accountNumber, String otp, LocalDateTime generatedAt) {
        this.accountNumber = accountNumber;
        this.otp = otp;
		log.debug(">>> Entering setGeneratedAt(generatedAt={})", generatedAt);
		log.debug(">>> Entering getGeneratedAt()");
		log.debug("<<< Exiting setGeneratedAt(generatedAt={})", generatedAt);
		log.debug("<<< Exiting getGeneratedAt()");
		log.debug(">>> Entering OtpInfo(accountNumber={},otp={},generatedAt={})", accountNumber, otp, generatedAt);
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): this.accountNumber → {}", accountNumber, otp, generatedAt, this.accountNumber);
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): accountNumber → {}", accountNumber, otp, generatedAt, accountNumber);
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): this.otp → {}", accountNumber, otp, generatedAt, this.otp);
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): otp → {}", accountNumber, otp, generatedAt, otp);
        this.generatedAt = generatedAt;
    }

}
