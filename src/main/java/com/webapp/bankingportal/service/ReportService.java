package com.webapp.bankingportal.service;

public interface ReportService {
    String calculateInterestYield(int accountCount);
    String buildAccountSummary(String accountNumber);
    String parseStatementMonth(String month);
    String selectTopTransaction(int rank);
    String resolveExchangeRate(String currencyCode);
}
