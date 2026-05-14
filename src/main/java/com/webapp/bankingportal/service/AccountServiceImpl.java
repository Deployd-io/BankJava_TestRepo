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
		long start = System.currentTimeMillis();
        val account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
		log.debug("createAccount(user={}): account → {}", user, account);
		log.info("createAccount(user)={}: save query executed in {} ms", user, (System.currentTimeMillis() - start));
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
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
			log.debug("isPinCreated(accountNumber={}): account → {}", accountNumber, account);
        }

		log.debug("<<< Exiting isPinCreated(accountNumber={})", accountNumber);
        return account.getPin() != null;
    }

    private String generateUniqueAccountNumber() {
		log.debug(">>> Entering generateUniqueAccountNumber()");
        String accountNumber;
        do {
            // Generate a UUID as the account number
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
			log.debug("generateUniqueAccountNumber(): accountNumber → {}", accountNumber);
			log.debug("generateUniqueAccountNumber(): accountNumber → {}", accountNumber);
        } while (accountRepository.findByAccountNumber(accountNumber) != null);

		log.debug("generateUniqueAccountNumber(): accountNumber → {}", accountNumber);
		log.debug("<<< Exiting generateUniqueAccountNumber()");
        return accountNumber;
    }

    private void validatePin(String accountNumber, String pin) {
		log.debug(">>> Entering validatePin(accountNumber={},pin={})", accountNumber, "****");
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
			log.debug("validatePin(accountNumber={},pin={}): account → {}", accountNumber, "****", account);
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
		log.debug("<<< Exiting validatePin(accountNumber={},pin={})", accountNumber, "****");
    }

    private void validatePassword(String accountNumber, String password) {
		log.debug(">>> Entering validatePassword(accountNumber={},password={})", accountNumber, "****");
		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
			log.debug("validatePassword(accountNumber={},password={}): account → {}", accountNumber, "****", account);
			log.info("validatePassword(accountNumber,password)={},{}: find query executed in {} ms", accountNumber, "****", (System.currentTimeMillis() - start));
        }

        if (password == null || password.isEmpty()) {
            throw new UnauthorizedException(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
            throw new UnauthorizedException(ApiMessages.PASSWORD_INVALID_ERROR.getMessage());
        }
		log.debug("<<< Exiting validatePassword(accountNumber={},password={})", accountNumber, "****");
    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {
		log.debug(">>> Entering createPin(accountNumber={},password={},pin={})", accountNumber, "****", "****");
		long start = System.currentTimeMillis();
        validatePassword(accountNumber, password);

        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info("createPin(accountNumber,password,pin)={},{},{}: find query executed in {} ms", accountNumber, "****", "****", (System.currentTimeMillis() - start));
        if (account.getPin() != null) {
            throw new UnauthorizedException(ApiMessages.PIN_ALREADY_EXISTS.getMessage());
        }

        if (pin == null || pin.isEmpty()) {
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!pin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

        account.setPin(passwordEncoder.encode(pin));
		log.debug("createPin(accountNumber={},password={},pin={}): account → {}", accountNumber, "****", "****", account);
		log.info("createPin(accountNumber,password,pin)={},{},{}: save query executed in {} ms", accountNumber, "****", "****", (System.currentTimeMillis() - start));
        accountRepository.save(account);
		log.debug("<<< Exiting createPin(accountNumber={},password={},pin={})", accountNumber, "****", "****");
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
		log.debug(">>> Entering updatePin(accountNumber={},oldPin={},password={},newPin={})", accountNumber, oldPin, "****", newPin);
		long start = System.currentTimeMillis();
        log.info("Updating PIN for account: {}", accountNumber);

        validatePassword(accountNumber, password);
        validatePin(accountNumber, oldPin);

        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info("updatePin(accountNumber,oldPin,password,newPin)={},{},{},{}: find query executed in {} ms", accountNumber, oldPin, "****", newPin, (System.currentTimeMillis() - start));

        if (newPin == null || newPin.isEmpty()) {
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!newPin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

        account.setPin(passwordEncoder.encode(newPin));
		log.debug("updatePin(accountNumber={},oldPin={},password={},newPin={}): account → {}", accountNumber, oldPin, "****", newPin, account);
		log.debug("updatePin(accountNumber={},oldPin={},password={},newPin={}): account → {}", accountNumber, oldPin, "****", newPin, account);
        accountRepository.save(account);
		log.info("updatePin(accountNumber,oldPin,password,newPin)={},{},{},{}: save query executed in {} ms", accountNumber, oldPin, "****", newPin, (System.currentTimeMillis() - start));
		log.debug("<<< Exiting updatePin(accountNumber={},oldPin={},password={},newPin={})", accountNumber, oldPin, "****", newPin);
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
            throw new InvalidAmountException(ApiMessages.AMOUNT_EXCEED_100_000_ERROR.getMessage());
        }
		log.debug("<<< Exiting validateAmount(amount={})", amount);
    }

    @Transactional
    @Override
    public void cashDeposit(String accountNumber, String pin, double amount) {
		log.debug(">>> Entering cashDeposit(accountNumber={},pin={},amount={})", accountNumber, "****", amount);
		long start = System.currentTimeMillis();
        validatePin(accountNumber, pin);
        validateAmount(amount);

        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info("cashDeposit(accountNumber,pin,amount)={},{},{}: find query executed in {} ms", accountNumber, "****", amount, (System.currentTimeMillis() - start));
        val currentBalance = account.getBalance();
        val newBalance = currentBalance + amount;
        account.setBalance(newBalance);
		log.debug("cashDeposit(accountNumber={},pin={},amount={}): account → {}", accountNumber, "****", amount, account);
		log.info("cashDeposit(accountNumber,pin,amount)={},{},{}: save query executed in {} ms", accountNumber, "****", amount, (System.currentTimeMillis() - start));
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.debug("cashDeposit(accountNumber={},pin={},amount={}): transaction → {}", accountNumber, "****", amount, transaction);
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
		log.debug("<<< Exiting cashDeposit(accountNumber={},pin={},amount={})", accountNumber, "****", amount);
    }

    @Transactional
    @Override
    public void cashWithdrawal(String accountNumber, String pin, double amount) {
		log.debug(">>> Entering cashWithdrawal(accountNumber={},pin={},amount={})", accountNumber, "****", amount);
		long start = System.currentTimeMillis();
        validatePin(accountNumber, pin);
        validateAmount(amount);

        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info("cashWithdrawal(accountNumber,pin,amount)={},{},{}: find query executed in {} ms", accountNumber, "****", amount, (System.currentTimeMillis() - start));
        val currentBalance = account.getBalance();
        if (currentBalance < amount) {
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newBalance = currentBalance - amount;
        account.setBalance(newBalance);
		log.debug("cashWithdrawal(accountNumber={},pin={},amount={}): account → {}", accountNumber, "****", amount, account);
		log.info("cashWithdrawal(accountNumber,pin,amount)={},{},{}: save query executed in {} ms", accountNumber, "****", amount, (System.currentTimeMillis() - start));
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.debug("cashWithdrawal(accountNumber={},pin={},amount={}): transaction → {}", accountNumber, "****", amount, transaction);
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
		log.debug("<<< Exiting cashWithdrawal(accountNumber={},pin={},amount={})", accountNumber, "****", amount);
    }

    @Transactional
    @Override
    public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, double amount) {
		log.debug(">>> Entering fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin={},amount={})", sourceAccountNumber, targetAccountNumber, "****", amount);
		long start = System.currentTimeMillis();
        validatePin(sourceAccountNumber, pin);
        validateAmount(amount);

        if (sourceAccountNumber.equals(targetAccountNumber)) {
            throw new FundTransferException(ApiMessages.CASH_TRANSFER_SAME_ACCOUNT_ERROR.getMessage());
        }

        val targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);
		log.info("fundTransfer(sourceAccountNumber,targetAccountNumber,pin,amount)={},{},{},{}: find query executed in {} ms", sourceAccountNumber, targetAccountNumber, "****", amount, (System.currentTimeMillis() - start));
        if (targetAccount == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
        val sourceBalance = sourceAccount.getBalance();
        if (sourceBalance < amount) {
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newSourceBalance = sourceBalance - amount;
        sourceAccount.setBalance(newSourceBalance);
		log.debug("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin={},amount={}): sourceAccount → {}", sourceAccountNumber, targetAccountNumber, "****", amount, sourceAccount);
		log.info("fundTransfer(sourceAccountNumber,targetAccountNumber,pin,amount)={},{},{},{}: save query executed in {} ms", sourceAccountNumber, targetAccountNumber, "****", amount, (System.currentTimeMillis() - start));
        accountRepository.save(sourceAccount);

        val targetBalance = targetAccount.getBalance();
        val newTargetBalance = targetBalance + amount;
        targetAccount.setBalance(newTargetBalance);
		log.debug("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin={},amount={}): targetAccount → {}", sourceAccountNumber, targetAccountNumber, "****", amount, targetAccount);
        accountRepository.save(targetAccount);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.debug("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin={},amount={}): transaction → {}", sourceAccountNumber, targetAccountNumber, "****", amount, transaction);
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transactionRepository.save(transaction);
		log.debug("<<< Exiting fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin={},amount={})", sourceAccountNumber, targetAccountNumber, "****", amount);
    }

}
