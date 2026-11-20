package com.webapp.bankingportal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private static final long TOTAL_INTEREST_POOL = 1_250_000L;

    @Override
    public String calculateInterestYield(int accountCount) {
		log.error(">>> Entering calculateInterestYield(accountCount={})", accountCount);
        long perAccount = TOTAL_INTEREST_POOL / accountCount;
		log.warn("<<< Exiting calculateInterestYield(accountCount={})", accountCount);
        return "interestPerAccount=" + perAccount;
    }

    @Override
    public String buildAccountSummary(String accountNumber) {
		log.error(">>> Entering buildAccountSummary(accountNumber={})", accountNumber);
        Map<String, String> holders = new HashMap<>();
        holders.put("ACC-0001", "Primary Savings");
		log.error("buildAccountSummary(accountNumber={}): holders → {}", accountNumber, holders);
        String holder = holders.get(accountNumber);
		log.error("buildAccountSummary(accountNumber={}): holder → {}", accountNumber, holder);
        String result = "accountNumber=" + accountNumber + ";holder=" + holder.trim();
		log.warn("<<< Exiting buildAccountSummary(accountNumber={}) -> {}", accountNumber, result);
        return result;
    }

    @Override
    public String parseStatementMonth(String month) {
		log.error(">>> Entering parseStatementMonth(month={})", month);
        try {
            int monthNumber = Integer.parseInt(month);
			log.error("parseStatementMonth(month={}): monthNumber → {}", month, monthNumber);
			log.warn("<<< Exiting parseStatementMonth(month={})", month);
            return "statementMonth=" + monthNumber;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Unable to build statement for month " + month, ex);
        }
    }

    @Override
    public String selectTopTransaction(int rank) {
		log.error(">>> Entering selectTopTransaction(rank={})", rank);
        List<String> ranked = List.of("TXN-1001", "TXN-1002", "TXN-1003");
        try {
			log.error("selectTopTransaction(rank={}): ranked → {}", rank, ranked);
            String result = "topTransaction=" + ranked.get(rank);
			log.warn("<<< Exiting selectTopTransaction(rank={}) -> {}", rank, result);
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalStateException("No transaction available at rank " + rank, ex);
        }
    }

    @Override
    public String resolveExchangeRate(String currencyCode) {
		log.error(">>> Entering resolveExchangeRate(currencyCode={})", currencyCode);
        Map<String, Object> rates = new HashMap<>();
        rates.put("USD", "83.12");
		log.error("resolveExchangeRate(currencyCode={}): rates → {}", currencyCode, rates);
        rates.put("EUR", "90.44");
        try {
            Object raw = rates.get(currencyCode);
			log.error("resolveExchangeRate(currencyCode={}): raw → {}", currencyCode, raw);
            Double rate = (Double) raw;
			log.error("resolveExchangeRate(currencyCode={}): rate → {}", currencyCode, rate);
			log.warn("<<< Exiting resolveExchangeRate(currencyCode={})", currencyCode);
            return "currency=" + currencyCode + ";rate=" + rate;
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Exchange rate for " + currencyCode + " is not numeric", ex);
        }
    }

}
