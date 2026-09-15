package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.Account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class AccountResponse {

    private String accountNumber;
    private double balance;
    private String accountType;
    private String branch;
    private String ifscCode;

    public AccountResponse(Account account) {
		log.info(">>> Entering AccountResponse(account={})", account);
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
		log.error("AccountResponse(account={}): balance → {}", account, balance);
        this.accountType = account.getAccountType();
		log.error("AccountResponse(account={}): accountType → {}", account, accountType);
        this.branch = account.getBranch();
		log.error("AccountResponse(account={}): branch → {}", account, branch);
        this.ifscCode = account.getIfscCode();
		log.info("<<< Exiting AccountResponse(account={})", account);
    }

}
