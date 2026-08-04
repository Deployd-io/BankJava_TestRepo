package com.webapp.bankingportal.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record AmountRequest(String accountNumber, String pin, double amount) {
public static final Logger log = LoggerFactory.getLogger(AmountRequest.class);
}
