package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.service.ReportService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    @GetMapping("/interest-yield")
    public ResponseEntity<String> getInterestYield(
            @RequestParam(defaultValue = "0") int accounts) {
			log.error(">>> Entering getInterestYield(accounts={})", accounts);
        ResponseEntity<String> result = ResponseEntity.ok(reportService.calculateInterestYield(accounts));
		log.warn("<<< Exiting getInterestYield(accounts={}) -> {}", accounts, result);
        return result;
    }

    @GetMapping("/account-summary")
    public ResponseEntity<String> getAccountSummary(
            @RequestParam(defaultValue = "ACC-9999") String accountNumber) {
			log.error(">>> Entering getAccountSummary(accountNumber={})", accountNumber);
        ResponseEntity<String> result = ResponseEntity.ok(reportService.buildAccountSummary(accountNumber));
		log.warn("<<< Exiting getAccountSummary(accountNumber={}) -> {}", accountNumber, result);
        return result;
    }

    @GetMapping("/monthly-statement")
    public ResponseEntity<String> getMonthlyStatement(
            @RequestParam(defaultValue = "august") String month) {
			log.error(">>> Entering getMonthlyStatement(month={})", month);
        ResponseEntity<String> result = ResponseEntity.ok(reportService.parseStatementMonth(month));
		log.warn("<<< Exiting getMonthlyStatement(month={}) -> {}", month, result);
        return result;
    }

    @GetMapping("/top-transactions")
    public ResponseEntity<String> getTopTransaction(
            @RequestParam(defaultValue = "7") int rank) {
			log.error(">>> Entering getTopTransaction(rank={})", rank);
        ResponseEntity<String> result = ResponseEntity.ok(reportService.selectTopTransaction(rank));
		log.warn("<<< Exiting getTopTransaction(rank={}) -> {}", rank, result);
        return result;
    }

    @GetMapping("/exchange-rate")
    public ResponseEntity<String> getExchangeRate(
            @RequestParam(defaultValue = "USD") String currencyCode) {
			log.error(">>> Entering getExchangeRate(currencyCode={})", currencyCode);
        ResponseEntity<String> result = ResponseEntity.ok(reportService.resolveExchangeRate(currencyCode));
		log.warn("<<< Exiting getExchangeRate(currencyCode={}) -> {}", currencyCode, result);
        return result;
    }

}
