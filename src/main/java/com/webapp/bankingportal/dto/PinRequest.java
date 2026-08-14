package com.webapp.bankingportal.dto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record PinRequest(String accountNumber, String pin, String password) {
}
