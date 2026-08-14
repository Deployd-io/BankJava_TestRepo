package com.webapp.bankingportal.dto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record FundTransferRequest(String sourceAccountNumber, String targetAccountNumber, double amount, String pin) {
}
