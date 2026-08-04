package com.webapp.bankingportal.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record OtpRequest(String identifier) {
public static final Logger log = LoggerFactory.getLogger(OtpRequest.class);
}
