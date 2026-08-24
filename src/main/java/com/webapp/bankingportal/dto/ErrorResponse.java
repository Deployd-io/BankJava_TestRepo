package com.webapp.bankingportal.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record ErrorResponse(String message) {
public static final Logger log = LoggerFactory.getLogger(ErrorResponse.class);
}
