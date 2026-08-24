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
public static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);
@Autowired
    private final UserRepository userRepository;
@Autowired
    private final AccountRepository accountRepository;

    @Override
    public UserResponse getUserDetails(String accountNumber) {
		log.debug(">>> Entering getUserDetails(accountNumber={})", accountNumber);
		long start = System.currentTimeMillis();
        val user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNumber)));
						log.info("getUserDetails(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));

		log.debug("<<< Exiting getUserDetails(accountNumber={})", accountNumber);
        return new UserResponse(user);
    }

    @Override
    public AccountResponse getAccountDetails(String accountNumber) {
		log.debug(">>> Entering getAccountDetails(accountNumber={})", accountNumber);
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
			log.debug("getAccountDetails(accountNumber={}): account → {}", accountNumber, account);
            throw new NotFoundException(String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNumber));
        }

		log.debug("<<< Exiting getAccountDetails(accountNumber={})", accountNumber);
        return new AccountResponse(account);
    }

}
