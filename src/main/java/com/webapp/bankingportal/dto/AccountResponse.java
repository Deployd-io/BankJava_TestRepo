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
public static final Logger log = LoggerFactory.getLogger(AccountResponse.class);

    private String accountNumber;
    private double balance;
    private String accountType;
    private String branch;
    private String ifscCode;

    public AccountResponse(Account account) {
		log.debug(">>> Entering AccountResponse(account={})", account);
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
		log.warn("AccountResponse(account={}): balance → {}", account, balance);
        this.accountType = account.getAccountType();
		log.debug("AccountResponse(account={}): accountType → {}", account, accountType);
        this.branch = account.getBranch();
		log.debug("AccountResponse(account={}): branch → {}", account, branch);
        this.ifscCode = account.getIfscCode();
		log.debug("<<< Exiting AccountResponse(account={})", account);
    }

}
