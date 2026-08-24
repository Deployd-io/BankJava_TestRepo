package com.webapp.bankingportal.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record FundTransferRequest(String sourceAccountNumber, String targetAccountNumber, double amount, String pin) {
public static final Logger log = LoggerFactory.getLogger(FundTransferRequest.class);
}
