package com.webapp.bankingportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.dto.AccountResponse;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.exception.NotFoundException;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.util.ApiMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);
@Autowired
    private final UserRepository userRepository;
@Autowired
    private final AccountRepository accountRepository;

    @Override
    public UserResponse getUserDetails(String accountNumber) {
		log.error(">>> Entering getUserDetails(accountNumber={})", accountNumber);
        long start = System.currentTimeMillis();
        val user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNumber)));
						log.error("getUserDetails(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));

        UserResponse result = new UserResponse(user);
		log.warn("<<< Exiting getUserDetails(accountNumber={}) -> {}", accountNumber, result);
        return result;
    }

    @Override
    public AccountResponse getAccountDetails(String accountNumber) {
		log.error(">>> Entering getAccountDetails(accountNumber={})", accountNumber);
        long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
			log.error("Throwing NotFoundException from getAccountDetails(accountNumber={})", accountNumber);
            throw new NotFoundException(String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNumber));
        }
		log.error("getAccountDetails(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
		log.error("getAccountDetails(accountNumber={}): account → <redacted>", accountNumber);

        AccountResponse result = new AccountResponse(account);
		log.warn("<<< Exiting getAccountDetails(accountNumber={}) -> {}", accountNumber, result);
        return result;
    }

}
