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
	log.debug("<<< Exiting getId()");
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	log.debug("<<< Exiting setId(id={})", id);
    private Long id;
	log.debug("setId(id={}): id → {}", id, id);

	log.debug(">>> Entering setId(id={})", id);
	log.debug(">>> Entering getId()");
	log.debug("<<< Exiting getAccountNumber()");
    @Column(unique = true)
    private String accountNumber;

	log.debug("<<< Exiting setAccountNumber(accountNumber={})", accountNumber);
	log.debug(">>> Entering setAccountNumber(accountNumber={})", accountNumber);
	log.debug(">>> Entering getAccountNumber()");
	log.debug("<<< Exiting getOtp()");
    @Column
    private String otp;

    @Column
	log.debug("<<< Exiting setOtp(otp={})", otp);
	log.debug(">>> Entering setOtp(otp={})", otp);
	log.debug(">>> Entering getOtp()");
	log.debug("<<< Exiting getGeneratedAt()");
	log.debug("<<< Exiting setGeneratedAt(generatedAt={})", generatedAt);
    private LocalDateTime generatedAt;

    public OtpInfo(String accountNumber, String otp, LocalDateTime generatedAt) {
        this.accountNumber = accountNumber;
        this.otp = otp;
		log.debug(">>> Entering setGeneratedAt(generatedAt={})", generatedAt);
		log.debug(">>> Entering getGeneratedAt()");
		log.debug(">>> Entering OtpInfo(accountNumber={},otp={},generatedAt={})", accountNumber, otp, generatedAt);
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): this.accountNumber → {}", accountNumber, otp, generatedAt, this.accountNumber);
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): accountNumber → {}", accountNumber, otp, generatedAt, accountNumber);
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): this.otp → {}", accountNumber, otp, generatedAt, this.otp);
		log.debug("OtpInfo(accountNumber={},otp={},generatedAt={}): otp → {}", accountNumber, otp, generatedAt, otp);
        this.generatedAt = generatedAt;
    }

}
