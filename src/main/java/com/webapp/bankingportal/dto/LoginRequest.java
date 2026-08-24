package com.webapp.bankingportal.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record LoginRequest(String identifier, String password) {
public static final Logger log = LoggerFactory.getLogger(LoginRequest.class);
}
