package com.webapp.bankingportal.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record ResetPasswordRequest(String identifier, String resetToken, String newPassword) {
public static final Logger log = LoggerFactory.getLogger(ResetPasswordRequest.class);
}
