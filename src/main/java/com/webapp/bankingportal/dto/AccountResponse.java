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
        this.accountNumber = account.getAccountNumber();
		log.info("ENTERING: AccountResponse() method");
        this.balance = account.getBalance();
        this.accountType = account.getAccountType();
        this.branch = account.getBranch();
        this.ifscCode = account.getIfscCode();
    }

}
