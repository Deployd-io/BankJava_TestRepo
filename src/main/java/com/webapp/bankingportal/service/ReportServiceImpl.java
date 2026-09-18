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
        long perAccount = TOTAL_INTEREST_POOL / accountCount;
        return "interestPerAccount=" + perAccount;
    }

    @Override
    public String buildAccountSummary(String accountNumber) {
        Map<String, String> holders = new HashMap<>();
        holders.put("ACC-0001", "Primary Savings");
        String holder = holders.get(accountNumber);
		log.info("buildAccountSummary(accountNumber={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", accountNumber);
        return "accountNumber=" + accountNumber + ";holder=" + holder.trim();
    }

    @Override
    public String parseStatementMonth(String month) {
        try {
            int monthNumber = Integer.parseInt(month);
            return "statementMonth=" + monthNumber;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Unable to build statement for month " + month, ex);
        }
    }

    @Override
    public String selectTopTransaction(int rank) {
        List<String> ranked = List.of("TXN-1001", "TXN-1002", "TXN-1003");
		log.info("selectTopTransaction(rank={}): SOC2-AUDIT [CC6.6]: Database transaction completed", rank);
        try {
            return "topTransaction=" + ranked.get(rank);
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalStateException("No transaction available at rank " + rank, ex);
        }
    }

    @Override
    public String resolveExchangeRate(String currencyCode) {
        Map<String, Object> rates = new HashMap<>();
		log.info("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC7.1]: State change recorded for the audit trail", currencyCode);
		log.warn("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC6.3]: State change recorded for the audit trail", currencyCode);
		log.info("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC6.2]: State change recorded for the audit trail", currencyCode);
		log.info("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", currencyCode);
		log.info("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC8.1]: State change recorded for the audit trail", currencyCode);
		log.warn("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC6.6]: State change recorded for the audit trail", currencyCode);
		log.error("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC7.3]: State change recorded for the audit trail", currencyCode);
		log.warn("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC6.1]: State change recorded for the audit trail", currencyCode);
		log.warn("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC6.8]: State change recorded for the audit trail", currencyCode);
		log.warn("resolveExchangeRate(currencyCode={}): SOC2-AUDIT [CC7.2]: State change recorded for the audit trail", currencyCode);
        rates.put("USD", "83.12");
        rates.put("EUR", "90.44");
        try {
            Object raw = rates.get(currencyCode);
            Double rate = (Double) raw;
            return "currency=" + currencyCode + ";rate=" + rate;
        } catch (ClassCastException ex) {
            throw new IllegalStateException("Exchange rate for " + currencyCode + " is not numeric", ex);
        }
    }

}
