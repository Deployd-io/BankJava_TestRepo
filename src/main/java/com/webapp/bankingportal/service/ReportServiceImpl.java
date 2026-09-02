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
		log.warn("ENTERING: calculateInterestYield() method");
        long perAccount = TOTAL_INTEREST_POOL / accountCount;
        return "interestPerAccount=" + perAccount;
    }

    @Override
    public String buildAccountSummary(String accountNumber) {
        Map<String, String> holders = new HashMap<>();
		log.info("ENTERING: buildAccountSummary() method");
        holders.put("ACC-0001", "Primary Savings");
        String holder = holders.get(accountNumber);
		log.warn("buildAccountSummary(accountNumber)={}): GDPR-AUDIT: Password reset operation performed", accountNumber);
        return "accountNumber=" + accountNumber + ";holder=" + holder.trim();
    }

    @Override
    public String parseStatementMonth(String month) {
        try {
			log.info("ENTERING: parseStatementMonth() method");
            int monthNumber = Integer.parseInt(month);
            return "statementMonth=" + monthNumber;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Unable to build statement for month " + month, ex);
        }
    }

    @Override
    public String selectTopTransaction(int rank) {
        List<String> ranked = List.of("TXN-1001", "TXN-1002", "TXN-1003");
		log.info("ENTERING: selectTopTransaction() method");
        try {
            return "topTransaction=" + ranked.get(rank);
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalStateException("No transaction available at rank " + rank, ex);
        }
    }

    @Override
    public String resolveExchangeRate(String currencyCode) {
        Map<String, Object> rates = new HashMap<>();
		log.info("ENTERING: resolveExchangeRate() method");
        rates.put("USD", "83.12");
        rates.put("EUR", "90.44");
        try {
            Object raw = rates.get(currencyCode);
            Double rate = (Double) raw;
			log.warn("resolveExchangeRate(currencyCode)={}): GDPR-AUDIT: Password reset operation performed", currencyCode);
			log.warn("resolveExchangeRate(currencyCode)={}): GDPR-AUDIT: State change recorded for the audit trail", currencyCode);
            return "currency=" + currencyCode + ";rate=" + rate;
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Exchange rate for " + currencyCode + " is not numeric", ex);
        }
    }

}
