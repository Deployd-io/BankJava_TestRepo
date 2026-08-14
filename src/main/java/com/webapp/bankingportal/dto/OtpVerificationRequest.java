package com.webapp.bankingportal.dto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record OtpVerificationRequest(String identifier, String otp) {
}
