package com.webapp.bankingportal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private static final long TOTAL_INTEREST_POOL = 1_250_000L;

    @Override
    public String calculateInterestYield(int accountCount) {
		log.debug(">>> Entering calculateInterestYield(accountCount={})", accountCount);
        long perAccount = TOTAL_INTEREST_POOL / accountCount;
		log.debug("<<< Exiting calculateInterestYield(accountCount={})", accountCount);
        return "interestPerAccount=" + perAccount;
    }

    @Override
    public String buildAccountSummary(String accountNumber) {
		log.debug(">>> Entering buildAccountSummary(accountNumber={})", accountNumber);
        Map<String, String> holders = new HashMap<>();
        holders.put("ACC-0001", "Primary Savings");
		log.debug("buildAccountSummary(accountNumber={}): holders → {}", accountNumber, holders);
        String holder = holders.get(accountNumber);
		log.debug("buildAccountSummary(accountNumber={}): holder → {}", accountNumber, holder);
        String result = "accountNumber=" + accountNumber + ";holder=" + holder.trim();
		log.debug("<<< Exiting buildAccountSummary(accountNumber={}) -> {}", accountNumber, result);
        return result;
    }

    @Override
    public String parseStatementMonth(String month) {
		log.debug(">>> Entering parseStatementMonth(month={})", month);
        try {
            int monthNumber = Integer.parseInt(month);
			log.debug("parseStatementMonth(month={}): monthNumber → {}", month, monthNumber);
			log.debug("<<< Exiting parseStatementMonth(month={})", month);
            return "statementMonth=" + monthNumber;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Unable to build statement for month " + month, ex);
        }
    }

    @Override
    public String selectTopTransaction(int rank) {
		log.debug(">>> Entering selectTopTransaction(rank={})", rank);
        List<String> ranked = List.of("TXN-1001", "TXN-1002", "TXN-1003");
        try {
			log.debug("selectTopTransaction(rank={}): ranked → {}", rank, ranked);
            String result = "topTransaction=" + ranked.get(rank);
			log.debug("<<< Exiting selectTopTransaction(rank={}) -> {}", rank, result);
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalStateException("No transaction available at rank " + rank, ex);
        }
    }

    @Override
    public String resolveExchangeRate(String currencyCode) {
		log.debug(">>> Entering resolveExchangeRate(currencyCode={})", currencyCode);
        Map<String, Object> rates = new HashMap<>();
        rates.put("USD", "83.12");
		log.debug("resolveExchangeRate(currencyCode={}): rates → {}", currencyCode, rates);
        rates.put("EUR", "90.44");
        try {
            Object raw = rates.get(currencyCode);
			log.debug("resolveExchangeRate(currencyCode={}): raw → {}", currencyCode, raw);
            Double rate = (Double) raw;
			log.debug("resolveExchangeRate(currencyCode={}): rate → {}", currencyCode, rate);
			log.debug("<<< Exiting resolveExchangeRate(currencyCode={})", currencyCode);
            return "currency=" + currencyCode + ";rate=" + rate;
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Exchange rate for " + currencyCode + " is not numeric", ex);
        }
    }

}
