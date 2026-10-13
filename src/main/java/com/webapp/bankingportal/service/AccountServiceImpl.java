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
		log.error("createAccount(user={}): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", user);
		log.warn("createAccount(user={}): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", user);
		log.info("createAccount(user={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", user);
		log.info("createAccount(user={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", user);
		log.warn("createAccount(user={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", user);
		log.info("createAccount(user={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", user);
        val account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(0.0);
        account.setUser(user);
        return accountRepository.save(account);
    }

    @Override
    public boolean isPinCreated(String accountNumber) {
		log.warn("isPinCreated(accountNumber={}): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", accountNumber);
		log.info("isPinCreated(accountNumber={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
		log.error("isPinCreated(accountNumber={}): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber);
		log.warn("isPinCreated(accountNumber={}): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber);
		log.info("isPinCreated(accountNumber={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.warn("isPinCreated(accountNumber={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
		log.info("isPinCreated(accountNumber={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        return account.getPin() != null;
    }

    private String generateUniqueAccountNumber() {
		log.warn("generateUniqueAccountNumber(): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail");
		log.warn("generateUniqueAccountNumber(): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail");
		log.warn("generateUniqueAccountNumber(): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail");
		log.error("generateUniqueAccountNumber(): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail");
		log.warn("generateUniqueAccountNumber(): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail");
		log.info("generateUniqueAccountNumber(): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail");
		log.info("generateUniqueAccountNumber(): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail");
		log.info("generateUniqueAccountNumber(): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail");
        String accountNumber;
        do {
            // Generate a UUID as the account number
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        } while (accountRepository.findByAccountNumber(accountNumber) != null);

        return accountNumber;
    }

    private void validatePin(String accountNumber, String pin) {
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.warn("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber);
		log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.6]: Database transaction completed", accountNumber);

        if (account.getPin() == null) {
			log.error("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber);
			log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
			log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", accountNumber, account);
            throw new UnauthorizedException(ApiMessages.PIN_NOT_CREATED.getMessage());
        }

        if (pin == null || pin.isEmpty()) {
			log.warn("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail", accountNumber);
			log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
			log.warn("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
			log.warn("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", accountNumber);
			log.warn("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", accountNumber);
			log.info("validatePin(accountNumber={}, pin=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);
            throw new UnauthorizedException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(pin, account.getPin())) {
            throw new UnauthorizedException(ApiMessages.PIN_INVALID_ERROR.getMessage());
        }
    }

    private void validatePassword(String accountNumber, String password) {
        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.6]: Database transaction completed", accountNumber);
        if (account == null) {
			log.warn("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail", accountNumber);
			log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
			log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", accountNumber, account);
			log.warn("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", accountNumber);
			log.warn("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", accountNumber);
			log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
			log.warn("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
			log.error("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber);
			log.warn("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber);
			log.info("validatePassword(accountNumber={}, password=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);
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

		log.warn("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", accountNumber);
		log.warn("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", accountNumber);
		log.warn("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail", accountNumber);
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account.getPin() != null) {
            throw new UnauthorizedException(ApiMessages.PIN_ALREADY_EXISTS.getMessage());
        }
		log.error("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber);
		log.warn("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber);

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
		log.warn("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
		log.info("createPin(accountNumber={}, password=<redacted>, pin=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);

        account.setPin(passwordEncoder.encode(pin));
        accountRepository.save(account);
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
        log.info("Updating PIN for account: {}", accountNumber);

        validatePassword(accountNumber, password);
		log.warn("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail", accountNumber);
		log.warn("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", accountNumber);
		log.warn("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", accountNumber);
		log.error("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber);
		log.warn("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber);
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
		log.warn("updatePin(accountNumber={}, oldPin=<redacted>, password=<redacted>, newPin=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
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
		log.warn("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail", accountNumber, amount);
		log.warn("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", accountNumber, amount);
		log.warn("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", accountNumber, amount);
		log.error("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber, amount);
		log.warn("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber, amount);
		log.warn("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber, amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.info("cashDeposit(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", accountNumber, amount, transaction);
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
		log.warn("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail", accountNumber, amount);
		log.warn("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", accountNumber, amount);
		log.warn("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", accountNumber, amount);
		log.error("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber, amount);
		log.warn("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber, amount);
		log.warn("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber, amount);
		log.info("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber, amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.info("cashWithdrawal(accountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", accountNumber, amount, transaction);
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
		log.warn("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.warn("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.warn("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.error("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.warn("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.7]: Encryption operation completed", sourceAccountNumber, targetAccountNumber, amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.warn("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", sourceAccountNumber, targetAccountNumber, amount);
        targetAccount.setBalance(newTargetBalance);
        accountRepository.save(targetAccount);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.info("fundTransfer(sourceAccountNumber={}, targetAccountNumber={}, pin=<redacted>, amount={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", sourceAccountNumber, targetAccountNumber, amount, transaction);
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transactionRepository.save(transaction);
    }

}
