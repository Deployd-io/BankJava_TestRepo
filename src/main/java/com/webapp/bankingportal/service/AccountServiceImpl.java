package com.webapp.bankingportal.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.entity.TransactionType;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.FundTransferException;
import com.webapp.bankingportal.exception.InsufficientBalanceException;
import com.webapp.bankingportal.exception.InvalidAmountException;
import com.webapp.bankingportal.exception.InvalidPinException;
import com.webapp.bankingportal.exception.NotFoundException;
import com.webapp.bankingportal.exception.UnauthorizedException;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.TransactionRepository;
import com.webapp.bankingportal.util.ApiMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
@Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final TransactionRepository transactionRepository;

    @Override
    public Account createAccount(User user) {
		log.debug(">>> Entering createAccount(user={})", user);
        val account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
		log.debug("createAccount(user={}): account → {}", user, account);
        account.setBalance(0.0);
        account.setUser(user);
		log.debug("<<< Exiting createAccount(user={})", user);
        return accountRepository.save(account);
    }

    @Override
    public boolean isPinCreated(String accountNumber) {
		log.debug(">>> Entering isPinCreated(accountNumber={})", accountNumber);
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
			log.debug("isPinCreated(accountNumber={}): account → {}", accountNumber, account);
		log.debug("<<< Exiting isPinCreated(accountNumber={})", accountNumber);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        return account.getPin() != null;
    }

    private String generateUniqueAccountNumber() {
		log.debug(">>> Entering generateUniqueAccountNumber()");
        String accountNumber;
        do {
            // Generate a UUID as the account number
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        } while (accountRepository.findByAccountNumber(accountNumber) != null);
		log.debug("generateUniqueAccountNumber(): accountNumber → {}", accountNumber);

		log.debug("<<< Exiting generateUniqueAccountNumber()");
        return accountNumber;
    }

    private void validatePin(String accountNumber, String pin) {
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
			log.debug("validatePin(accountNumber={},pin=<redacted>): account → {}", accountNumber, account);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        if (account.getPin() == null) {
            throw new UnauthorizedException(ApiMessages.PIN_NOT_CREATED.getMessage());
        }

        if (pin == null || pin.isEmpty()) {
            throw new UnauthorizedException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(pin, account.getPin())) {
            throw new UnauthorizedException(ApiMessages.PIN_INVALID_ERROR.getMessage());
        }
    }

    private void validatePassword(String accountNumber, String password) {
		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
			log.info("validatePassword(accountNumber,password)={},{}: find query executed in {} ms", accountNumber, password, (System.currentTimeMillis() - start));
			log.debug("validatePassword(accountNumber={},password=<redacted>): account → {}", accountNumber, account);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        if (password == null || password.isEmpty()) {
            throw new UnauthorizedException(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
            throw new UnauthorizedException(ApiMessages.PASSWORD_INVALID_ERROR.getMessage());
        }
    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {
        validatePassword(accountNumber, password);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account.getPin() != null) {
			log.debug("createPin(accountNumber={},password=<redacted>,pin=<redacted>): account → {}", accountNumber, account);
            throw new UnauthorizedException(ApiMessages.PIN_ALREADY_EXISTS.getMessage());
        }
		log.info("createPin(accountNumber,password,pin)={},{},{}: find query executed in {} ms", accountNumber, password, pin, (System.currentTimeMillis() - start));

        if (pin == null || pin.isEmpty()) {
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!pin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

        account.setPin(passwordEncoder.encode(pin));
		log.info("createPin(accountNumber,password,pin)={},{},{}: save query executed in {} ms", accountNumber, password, pin, (System.currentTimeMillis() - start));
        accountRepository.save(account);
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
        log.info("Updating PIN for account: {}", accountNumber);

        validatePassword(accountNumber, password);
        validatePin(accountNumber, oldPin);

		long start = System.currentTimeMillis();
		log.info("updatePin(accountNumber,oldPin,password,newPin)={},{},{},{}: find query executed in {} ms", accountNumber, oldPin, password, newPin, (System.currentTimeMillis() - start));
        val account = accountRepository.findByAccountNumber(accountNumber);

        if (newPin == null || newPin.isEmpty()) {
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!newPin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

		log.info("updatePin(accountNumber,oldPin,password,newPin)={},{},{},{}: save query executed in {} ms", accountNumber, oldPin, password, newPin, (System.currentTimeMillis() - start));
        account.setPin(passwordEncoder.encode(newPin));
        accountRepository.save(account);
    }

    private void validateAmount(double amount) {
		log.debug(">>> Entering validateAmount(amount={})", amount);
        if (amount <= 0) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage());
        }

        if (amount % 100 != 0) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_NOT_MULTIPLE_OF_100_ERROR.getMessage());
        }

        if (amount > 100000) {
		log.debug("<<< Exiting validateAmount(amount={})", amount);
            throw new InvalidAmountException(ApiMessages.AMOUNT_EXCEED_100_000_ERROR.getMessage());
        }
    }

    @Transactional
    @Override
    public void cashDeposit(String accountNumber, String pin, double amount) {
        validatePin(accountNumber, pin);
        validateAmount(amount);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
		log.info("cashDeposit(accountNumber,pin,amount)={},{},{}: find query executed in {} ms", accountNumber, pin, amount, (System.currentTimeMillis() - start));
		log.info("cashDeposit(accountNumber,pin,amount)={},{},{}: save query executed in {} ms", accountNumber, pin, amount, (System.currentTimeMillis() - start));
        val newBalance = currentBalance + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void cashWithdrawal(String accountNumber, String pin, double amount) {
        validatePin(accountNumber, pin);
        validateAmount(amount);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
		log.info("cashWithdrawal(accountNumber,pin,amount)={},{},{}: find query executed in {} ms", accountNumber, pin, amount, (System.currentTimeMillis() - start));
        if (currentBalance < amount) {
			log.debug("cashWithdrawal(accountNumber={},pin=<redacted>,amount={}): currentBalance → {}", accountNumber, amount, currentBalance);
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newBalance = currentBalance - amount;
        account.setBalance(newBalance);
        accountRepository.save(account);
		log.info("cashWithdrawal(accountNumber,pin,amount)={},{},{}: save query executed in {} ms", accountNumber, pin, amount, (System.currentTimeMillis() - start));

        val transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, double amount) {
        validatePin(sourceAccountNumber, pin);
        validateAmount(amount);

        if (sourceAccountNumber.equals(targetAccountNumber)) {
            throw new FundTransferException(ApiMessages.CASH_TRANSFER_SAME_ACCOUNT_ERROR.getMessage());
        }

		long start = System.currentTimeMillis();
        val targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);
        if (targetAccount == null) {
			log.info("fundTransfer(sourceAccountNumber,targetAccountNumber,pin,amount)={},{},{},{}: find query executed in {} ms", sourceAccountNumber, targetAccountNumber, pin, amount, (System.currentTimeMillis() - start));
			log.debug("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): targetAccount → {}", sourceAccountNumber, targetAccountNumber, amount, targetAccount);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
        val sourceBalance = sourceAccount.getBalance();
        if (sourceBalance < amount) {
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newSourceBalance = sourceBalance - amount;
        sourceAccount.setBalance(newSourceBalance);
        accountRepository.save(sourceAccount);
		log.info("fundTransfer(sourceAccountNumber,targetAccountNumber,pin,amount)={},{},{},{}: save query executed in {} ms", sourceAccountNumber, targetAccountNumber, pin, amount, (System.currentTimeMillis() - start));

        val targetBalance = targetAccount.getBalance();
        val newTargetBalance = targetBalance + amount;
        targetAccount.setBalance(newTargetBalance);
        accountRepository.save(targetAccount);

        val transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transactionRepository.save(transaction);
    }

}
