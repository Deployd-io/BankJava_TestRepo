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
			log.debug(">>> Entering getInterestYield()");
		log.debug("<<< Exiting getInterestYield()");
        return ResponseEntity.ok(reportService.calculateInterestYield(accounts));
    }

    @GetMapping("/account-summary")
    public ResponseEntity<String> getAccountSummary(
            @RequestParam(defaultValue = "ACC-9999") String accountNumber) {
			log.debug(">>> Entering getAccountSummary()");
		log.debug("<<< Exiting getAccountSummary()");
        return ResponseEntity.ok(reportService.buildAccountSummary(accountNumber));
    }

    @GetMapping("/monthly-statement")
    public ResponseEntity<String> getMonthlyStatement(
            @RequestParam(defaultValue = "august") String month) {
			log.debug(">>> Entering getMonthlyStatement()");
		log.debug("<<< Exiting getMonthlyStatement()");
        return ResponseEntity.ok(reportService.parseStatementMonth(month));
    }

    @GetMapping("/top-transactions")
    public ResponseEntity<String> getTopTransaction(
            @RequestParam(defaultValue = "7") int rank) {
			log.debug(">>> Entering getTopTransaction()");
		log.debug("<<< Exiting getTopTransaction()");
        return ResponseEntity.ok(reportService.selectTopTransaction(rank));
    }

    @GetMapping("/exchange-rate")
    public ResponseEntity<String> getExchangeRate(
            @RequestParam(defaultValue = "USD") String currencyCode) {
			log.debug(">>> Entering getExchangeRate()");
		log.debug("<<< Exiting getExchangeRate()");
        return ResponseEntity.ok(reportService.resolveExchangeRate(currencyCode));
    }

}
