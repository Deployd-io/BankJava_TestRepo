package com.webapp.bankingportal.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record PinRequest(String accountNumber, String pin, String password) {
public static final Logger log = LoggerFactory.getLogger(PinRequest.class);
}
