package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.service.ReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/interest-yield")
    public ResponseEntity<String> getInterestYield(
            @RequestParam(defaultValue = "0") int accounts) {
			log.error(">>> Entering getInterestYield()");
        ResponseEntity<String> result = ResponseEntity.ok(reportService.calculateInterestYield(accounts));
		log.warn("<<< Exiting getInterestYield() -> {}", result);
        return result;
    }

    @GetMapping("/account-summary")
    public ResponseEntity<String> getAccountSummary(
            @RequestParam(defaultValue = "ACC-9999") String accountNumber) {
			log.error(">>> Entering getAccountSummary()");
        ResponseEntity<String> result = ResponseEntity.ok(reportService.buildAccountSummary(accountNumber));
		log.warn("<<< Exiting getAccountSummary() -> {}", result);
        return result;
    }

    @GetMapping("/monthly-statement")
    public ResponseEntity<String> getMonthlyStatement(
            @RequestParam(defaultValue = "august") String month) {
			log.error(">>> Entering getMonthlyStatement()");
        ResponseEntity<String> result = ResponseEntity.ok(reportService.parseStatementMonth(month));
		log.warn("<<< Exiting getMonthlyStatement() -> {}", result);
        return result;
    }

    @GetMapping("/top-transactions")
    public ResponseEntity<String> getTopTransaction(
            @RequestParam(defaultValue = "7") int rank) {
			log.error(">>> Entering getTopTransaction()");
        ResponseEntity<String> result = ResponseEntity.ok(reportService.selectTopTransaction(rank));
		log.warn("<<< Exiting getTopTransaction() -> {}", result);
        return result;
    }

    @GetMapping("/exchange-rate")
    public ResponseEntity<String> getExchangeRate(
            @RequestParam(defaultValue = "USD") String currencyCode) {
			log.error(">>> Entering getExchangeRate()");
        ResponseEntity<String> result = ResponseEntity.ok(reportService.resolveExchangeRate(currencyCode));
		log.warn("<<< Exiting getExchangeRate() -> {}", result);
        return result;
    }

}
