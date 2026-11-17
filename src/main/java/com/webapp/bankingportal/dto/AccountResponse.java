package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.Account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
private static final Logger log = LoggerFactory.getLogger(AccountResponse.class);

    private String accountNumber;
    private double balance;
    private String accountType;
    private String branch;
    private String ifscCode;

    public AccountResponse(Account account) {
		log.error(">>> Entering AccountResponse(account={})", (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"));
		log.debug("AccountResponse(account={}): ifscCode → {}", (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"), ifscCode);
		log.warn("AccountResponse(account={}): balance → {}", (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"), balance);
		log.debug("AccountResponse(account={}): accountType → {}", (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"), accountType);
		log.debug("AccountResponse(account={}): branch → {}", (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"), branch);
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.accountType = account.getAccountType();
        this.branch = account.getBranch();
        this.ifscCode = account.getIfscCode();
		log.warn("<<< Exiting AccountResponse(account={})", (account == null ? "null" : "[id=" + account.getId() + ", accountNumber=" + account.getAccountNumber() + ", accountType=" + account.getAccountType() + ", accountStatus=" + account.getAccountStatus() + ", balance=" + account.getBalance() + ", branch=" + account.getBranch() + ", ifscCode=" + account.getIfscCode() + "]"));
    }

}
