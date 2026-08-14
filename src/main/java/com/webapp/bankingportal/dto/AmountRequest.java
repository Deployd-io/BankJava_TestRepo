package com.webapp.bankingportal.dto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record AmountRequest(String accountNumber, String pin, double amount) {
}
