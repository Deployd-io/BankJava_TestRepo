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
		log.error(">>> Entering createAccount(user={})", user);
        val account = new Account();
		log.debug("createAccount(user={}): account → {}", user, account);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(0.0);
        account.setUser(user);
		log.error("createAccount(user={}): account → {}", user, account);
        Account result = accountRepository.save(account);
		log.warn("<<< Exiting createAccount(user={}) -> {}", user, result);
        return result;
    }

    @Override
    public boolean isPinCreated(String accountNumber) {
		log.error(">>> Entering isPinCreated(accountNumber={})", accountNumber);
        long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
			log.error("Throwing NotFoundException from isPinCreated(accountNumber={})", accountNumber);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.error("isPinCreated(accountNumber={}): account → {}", accountNumber, account);

        boolean result = account.getPin() != null;
		log.warn("<<< Exiting isPinCreated(accountNumber={})", accountNumber);
        return result;
    }

    private String generateUniqueAccountNumber() {
		log.error(">>> Entering generateUniqueAccountNumber()");
		long start = System.currentTimeMillis();
        String accountNumber;
        do {
            // Generate a UUID as the account number
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
			log.debug("generateUniqueAccountNumber(): accountNumber → {}", accountNumber);
        } while (accountRepository.findByAccountNumber(accountNumber) != null);
		log.error("generateUniqueAccountNumber(): find query executed in {} ms", (System.currentTimeMillis() - start));
		log.error("generateUniqueAccountNumber(): accountNumber → {}", accountNumber);

		log.warn("<<< Exiting generateUniqueAccountNumber()");
        return accountNumber;
    }

    private void validatePin(String accountNumber, String pin) {
		log.error(">>> Entering validatePin(accountNumber={},pin=<redacted>)", accountNumber);
        long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
			log.error("Throwing NotFoundException from validatePin(accountNumber={},pin=<redacted>)", accountNumber);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.error("validatePin(accountNumber={},pin=<redacted>): account → {}", accountNumber, account);

        if (account.getPin() == null) {
			log.error("Throwing UnauthorizedException from validatePin(accountNumber={},pin=<redacted>)", accountNumber);
            throw new UnauthorizedException(ApiMessages.PIN_NOT_CREATED.getMessage());
        }

        if (pin == null || pin.isEmpty()) {
            throw new UnauthorizedException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(pin, account.getPin())) {
            throw new UnauthorizedException(ApiMessages.PIN_INVALID_ERROR.getMessage());
        }
		log.warn("<<< Exiting validatePin(accountNumber={},pin=<redacted>)", accountNumber);
    }

    private void validatePassword(String accountNumber, String password) {
		log.error(">>> Entering validatePassword(accountNumber={},password=<redacted>)", accountNumber);
        long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
			log.error("Throwing NotFoundException from validatePassword(accountNumber={},password=<redacted>)", accountNumber);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.error("validatePassword(accountNumber={},password=<redacted>): account → {}", accountNumber, account);

        if (password == null || password.isEmpty()) {
			log.error("Throwing UnauthorizedException from validatePassword(accountNumber={},password=<redacted>)", accountNumber);
            throw new UnauthorizedException(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
            throw new UnauthorizedException(ApiMessages.PASSWORD_INVALID_ERROR.getMessage());
        }
		log.warn("<<< Exiting validatePassword(accountNumber={},password=<redacted>)", accountNumber);
    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {
		log.error(">>> Entering createPin(accountNumber={},password=<redacted>,pin=<redacted>)", accountNumber);
        validatePassword(accountNumber, password);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account.getPin() != null) {
			log.error("Throwing UnauthorizedException from createPin(accountNumber={},password=<redacted>,pin=<redacted>)", accountNumber);
            throw new UnauthorizedException(ApiMessages.PIN_ALREADY_EXISTS.getMessage());
        }
		log.error("createPin(accountNumber,password,pin)={},<redacted>,<redacted>: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
		log.error("createPin(accountNumber={},password=<redacted>,pin=<redacted>): account → {}", accountNumber, account);

        if (pin == null || pin.isEmpty()) {
			log.error("Throwing InvalidPinException from createPin(accountNumber={},password=<redacted>,pin=<redacted>)", accountNumber);
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!pin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

        account.setPin(passwordEncoder.encode(pin));
		log.debug("createPin(accountNumber={},password=<redacted>,pin=<redacted>): account → {}", accountNumber, account);
        accountRepository.save(account);
		log.error("createPin(accountNumber,password,pin)={},<redacted>,<redacted>: save query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
    log.warn("<<< Exiting createPin(accountNumber={},password=<redacted>,pin=<redacted>)", accountNumber);
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
		log.error(">>> Entering updatePin(accountNumber={},oldPin=<redacted>,password=<redacted>,newPin=<redacted>)", accountNumber);
        log.info("Updating PIN for account: {}", accountNumber);

        validatePassword(accountNumber, password);
        validatePin(accountNumber, oldPin);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
		log.error("updatePin(accountNumber,oldPin,password,newPin)={},<redacted>,<redacted>,<redacted>: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));

        if (newPin == null || newPin.isEmpty()) {
			log.error("Throwing InvalidPinException from updatePin(accountNumber={},oldPin=<redacted>,password=<redacted>,newPin=<redacted>)", accountNumber);
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!newPin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

        account.setPin(passwordEncoder.encode(newPin));
		log.debug("updatePin(accountNumber={},oldPin=<redacted>,password=<redacted>,newPin=<redacted>): account → {}", accountNumber, account);
        accountRepository.save(account);
		log.error("updatePin(accountNumber,oldPin,password,newPin)={},<redacted>,<redacted>,<redacted>: save query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
    log.warn("<<< Exiting updatePin(accountNumber={},oldPin=<redacted>,password=<redacted>,newPin=<redacted>)", accountNumber);
    }

    private void validateAmount(double amount) {
		log.error(">>> Entering validateAmount(amount={})", amount);
        if (amount <= 0) {
			log.error("Throwing InvalidAmountException from validateAmount(amount={})", amount);
            throw new InvalidAmountException(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage());
        }

        if (amount % 100 != 0) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_NOT_MULTIPLE_OF_100_ERROR.getMessage());
        }

        if (amount > 100000) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_EXCEED_100_000_ERROR.getMessage());
        }
		log.warn("<<< Exiting validateAmount(amount={})", amount);
    }

    @Transactional
    @Override
    public void cashDeposit(String accountNumber, String pin, double amount) {
		log.error(">>> Entering cashDeposit(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
        validatePin(accountNumber, pin);
        validateAmount(amount);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
		log.error("cashDeposit(accountNumber={},pin=<redacted>,amount={}): account → {}", accountNumber, amount, account);
		log.error("cashDeposit(accountNumber={},pin=<redacted>,amount={}): currentBalance → {}", accountNumber, amount, currentBalance);
		log.debug("cashDeposit(accountNumber={},pin=<redacted>,amount={}): account → {}", accountNumber, amount, account);
        val newBalance = currentBalance + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);
		log.error("cashDeposit(accountNumber,pin,amount)={},<redacted>,{}: find query executed in {} ms", accountNumber, amount, (System.currentTimeMillis() - start));
		log.error("cashDeposit(accountNumber,pin,amount)={},<redacted>,{}: save query executed in {} ms", accountNumber, amount, (System.currentTimeMillis() - start));

        val transaction = new Transaction();
		log.error("cashDeposit(accountNumber={},pin=<redacted>,amount={}): transaction → {}", accountNumber, amount, transaction);
		log.debug("cashDeposit(accountNumber={},pin=<redacted>,amount={}): transaction → {}", accountNumber, amount, transaction);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
		log.warn("<<< Exiting cashDeposit(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
    }

    @Transactional
    @Override
    public void cashWithdrawal(String accountNumber, String pin, double amount) {
		log.error(">>> Entering cashWithdrawal(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
        validatePin(accountNumber, pin);
        validateAmount(amount);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
		log.error("cashWithdrawal(accountNumber={},pin=<redacted>,amount={}): account → {}", accountNumber, amount, account);
        if (currentBalance < amount) {
			log.error("Throwing InsufficientBalanceException from cashWithdrawal(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }
		log.error("cashWithdrawal(accountNumber,pin,amount)={},<redacted>,{}: find query executed in {} ms", accountNumber, amount, (System.currentTimeMillis() - start));
		log.error("cashWithdrawal(accountNumber={},pin=<redacted>,amount={}): currentBalance → {}", accountNumber, amount, currentBalance);

        val newBalance = currentBalance - amount;
        account.setBalance(newBalance);
		log.debug("cashWithdrawal(accountNumber={},pin=<redacted>,amount={}): account → {}", accountNumber, amount, account);
        accountRepository.save(account);
		log.error("cashWithdrawal(accountNumber,pin,amount)={},<redacted>,{}: save query executed in {} ms", accountNumber, amount, (System.currentTimeMillis() - start));

        val transaction = new Transaction();
		log.error("cashWithdrawal(accountNumber={},pin=<redacted>,amount={}): transaction → {}", accountNumber, amount, transaction);
		log.debug("cashWithdrawal(accountNumber={},pin=<redacted>,amount={}): transaction → {}", accountNumber, amount, transaction);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
		log.warn("<<< Exiting cashWithdrawal(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
    }

    @Transactional
    @Override
    public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, double amount) {
		log.error(">>> Entering fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
        validatePin(sourceAccountNumber, pin);
        validateAmount(amount);

        if (sourceAccountNumber.equals(targetAccountNumber)) {
			log.error("Throwing FundTransferException from fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
            throw new FundTransferException(ApiMessages.CASH_TRANSFER_SAME_ACCOUNT_ERROR.getMessage());
        }

		long start = System.currentTimeMillis();
        val targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);
        if (targetAccount == null) {
			log.error("Throwing NotFoundException from fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.error("fundTransfer(sourceAccountNumber,targetAccountNumber,pin,amount)={},{},<redacted>,{}: find query executed in {} ms", sourceAccountNumber, targetAccountNumber, amount, (System.currentTimeMillis() - start));
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): targetAccount → {}", sourceAccountNumber, targetAccountNumber, amount, targetAccount);

        val sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): sourceAccount → {}", sourceAccountNumber, targetAccountNumber, amount, sourceAccount);
        val sourceBalance = sourceAccount.getBalance();
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): sourceBalance → {}", sourceAccountNumber, targetAccountNumber, amount, sourceBalance);
        if (sourceBalance < amount) {
			log.error("Throwing InsufficientBalanceException from fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newSourceBalance = sourceBalance - amount;
        sourceAccount.setBalance(newSourceBalance);
		log.debug("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): sourceAccount → {}", sourceAccountNumber, targetAccountNumber, amount, sourceAccount);
        accountRepository.save(sourceAccount);
		log.error("fundTransfer(sourceAccountNumber,targetAccountNumber,pin,amount)={},{},<redacted>,{}: save query executed in {} ms", sourceAccountNumber, targetAccountNumber, amount, (System.currentTimeMillis() - start));

        val targetBalance = targetAccount.getBalance();
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): targetBalance → {}", sourceAccountNumber, targetAccountNumber, amount, targetBalance);
        val newTargetBalance = targetBalance + amount;
        targetAccount.setBalance(newTargetBalance);
		log.debug("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): targetAccount → {}", sourceAccountNumber, targetAccountNumber, amount, targetAccount);
        accountRepository.save(targetAccount);

        val transaction = new Transaction();
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): transaction → {}", sourceAccountNumber, targetAccountNumber, amount, transaction);
		log.debug("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): transaction → {}", sourceAccountNumber, targetAccountNumber, amount, transaction);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transactionRepository.save(transaction);
		log.warn("<<< Exiting fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
    }

}
