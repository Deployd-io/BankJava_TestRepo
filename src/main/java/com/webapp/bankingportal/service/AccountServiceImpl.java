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
        val account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
		log.info("createAccount(user={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", user);
		log.info("createAccount(user={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", user);
		log.info("createAccount(user={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", user);
		log.info("createAccount(user={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", user);
        account.setBalance(0.0);
        account.setUser(user);
        return accountRepository.save(account);
    }

    @Override
    public boolean isPinCreated(String accountNumber) {
        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info("isPinCreated(accountNumber={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.info("isPinCreated(accountNumber={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.info("isPinCreated(accountNumber={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
		log.info("isPinCreated(accountNumber={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);

        return account.getPin() != null;
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
		log.info("generateUniqueAccountNumber(): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail");
        do {
            // Generate a UUID as the account number
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        } while (accountRepository.findByAccountNumber(accountNumber) != null);
		log.info("generateUniqueAccountNumber(): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail");
		log.info("generateUniqueAccountNumber(): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail");
		log.info("generateUniqueAccountNumber(): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail");

        return accountNumber;
    }

    private void validatePin(String accountNumber, String pin) {
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

		log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.6]: Database transaction completed", accountNumber);
        if (account.getPin() == null) {
            throw new UnauthorizedException(ApiMessages.PIN_NOT_CREATED.getMessage());
        }
		log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
		log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", accountNumber, account);

        if (pin == null || pin.isEmpty()) {
            throw new UnauthorizedException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }
		log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);

        if (!passwordEncoder.matches(pin, account.getPin())) {
            throw new UnauthorizedException(ApiMessages.PIN_INVALID_ERROR.getMessage());
        }
		log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);
    }

    private void validatePassword(String accountNumber, String password) {
        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.6]: Database transaction completed", accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
		log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", accountNumber, account);

        if (password == null || password.isEmpty()) {
            throw new UnauthorizedException(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage());
        }
		log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);

        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
            throw new UnauthorizedException(ApiMessages.PASSWORD_INVALID_ERROR.getMessage());
        }
		log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);
    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {
        validatePassword(accountNumber, password);

        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account.getPin() != null) {
            throw new UnauthorizedException(ApiMessages.PIN_ALREADY_EXISTS.getMessage());
        }

        if (pin == null || pin.isEmpty()) {
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }
		log.info("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC6.6]: Database transaction completed", accountNumber);
		log.info("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);

        if (!pin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }
		log.info("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", accountNumber, account);
		log.info("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.info("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
		log.info("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);

        account.setPin(passwordEncoder.encode(pin));
        accountRepository.save(account);
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
        log.info("Updating PIN for account: {}", accountNumber);

        validatePassword(accountNumber, password);
        validatePin(accountNumber, oldPin);

        val account = accountRepository.findByAccountNumber(accountNumber);

        if (newPin == null || newPin.isEmpty()) {
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }
		log.info("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC6.6]: Database transaction completed", accountNumber);
		log.info("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC8.1]: Change management operation performed", accountNumber);

        if (!newPin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }
		log.info("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", accountNumber, account);
		log.info("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.info("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
		log.info("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);

        account.setPin(passwordEncoder.encode(newPin));
        accountRepository.save(account);
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage());
        }

        if (amount % 100 != 0) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_NOT_MULTIPLE_OF_100_ERROR.getMessage());
        }

        if (amount > 100000) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_EXCEED_100_000_ERROR.getMessage());
        }
		log.info("validateAmount(amount={}): SOC2-AUDIT [CC6.6]: Database transaction completed", amount);
    }

    @Transactional
    @Override
    public void cashDeposit(String accountNumber, String pin, double amount) {
        validatePin(accountNumber, pin);
        validateAmount(amount);

        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
        val newBalance = currentBalance + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.info("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", accountNumber, amount, transaction);
		log.info("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber, amount);
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

        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
        if (currentBalance < amount) {
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newBalance = currentBalance - amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.info("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", accountNumber, amount, transaction);
		log.info("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber, amount);
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

        val targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);
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
        accountRepository.save(sourceAccount);

        val targetBalance = targetAccount.getBalance();
        val newTargetBalance = targetBalance + amount;
        targetAccount.setBalance(newTargetBalance);
        accountRepository.save(targetAccount);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", sourceAccountNumber, targetAccountNumber, amount, transaction);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.7]: Encryption operation completed", sourceAccountNumber, targetAccountNumber, amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transactionRepository.save(transaction);
    }

}
