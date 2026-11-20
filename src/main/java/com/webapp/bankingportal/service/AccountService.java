package com.webapp.bankingportal.service;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AccountService {
Logger log = LoggerFactory.getLogger(AccountService.class);

	public Account createAccount(User user);
	public boolean isPinCreated(String accountNumber) ;
	public void createPin(String accountNumber, String password, String pin) ;
	public void updatePin(String accountNumber, String oldPIN, String password, String newPIN);
	public void cashDeposit(String accountNumber, String pin, double amount, String channel);
	public default void cashDeposit(String accountNumber, String pin, double amount) {
		log.error(">>> Entering cashDeposit(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
		cashDeposit(accountNumber, pin, amount, "API");
		log.warn("<<< Exiting cashDeposit(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
	}
	public void cashWithdrawal(String accountNumber, String pin, double amount);
	public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, double amount);
	
	
}
