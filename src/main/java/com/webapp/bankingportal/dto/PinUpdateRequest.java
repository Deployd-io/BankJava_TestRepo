package com.webapp.bankingportal.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record PinUpdateRequest(String accountNumber, String oldPin, String newPin, String password) {
public static final Logger log = LoggerFactory.getLogger(PinUpdateRequest.class);
}
