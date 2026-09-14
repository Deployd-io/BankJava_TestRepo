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
		log.info(">>> Entering createAccount(user={})", user);
        val account = new Account();
		log.error("createAccount(user={}): account → {}", user, account);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(0.0);
        account.setUser(user);
		log.info("<<< Exiting createAccount(user={})", user);
        return accountRepository.save(account);
    }

    @Override
    public boolean isPinCreated(String accountNumber) {
        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info(">>> Entering isPinCreated(accountNumber={})", accountNumber);
        if (account == null) {
			log.debug("Throwing NotFoundException from isPinCreated(accountNumber={})", accountNumber);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.info("isPinCreated(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
		log.error("isPinCreated(accountNumber={}): account → {}", accountNumber, account);

		log.info("<<< Exiting isPinCreated(accountNumber={})", accountNumber);
        return account.getPin() != null;
    }

    private String generateUniqueAccountNumber() {
		log.info(">>> Entering generateUniqueAccountNumber()");
		long start = System.currentTimeMillis();
        String accountNumber;
        do {
            // Generate a UUID as the account number
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
			log.error("generateUniqueAccountNumber(): accountNumber → {}", accountNumber);
        } while (accountRepository.findByAccountNumber(accountNumber) != null);
		log.info("generateUniqueAccountNumber(): find query executed in {} ms", (System.currentTimeMillis() - start));

		log.info("<<< Exiting generateUniqueAccountNumber()");
        return accountNumber;
    }

    private void validatePin(String accountNumber, String pin) {
        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info(">>> Entering validatePin(accountNumber={},pin=<redacted>)", accountNumber);
        if (account == null) {
			log.debug("Throwing NotFoundException from validatePin(accountNumber={},pin=<redacted>)", accountNumber);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.info("validatePin(accountNumber,pin)={},<redacted>: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
		log.error("validatePin(accountNumber={},pin=<redacted>): account → {}", accountNumber, account);

        if (account.getPin() == null) {
			log.debug("Throwing UnauthorizedException from validatePin(accountNumber={},pin=<redacted>)", accountNumber);
            throw new UnauthorizedException(ApiMessages.PIN_NOT_CREATED.getMessage());
        }

        if (pin == null || pin.isEmpty()) {
            throw new UnauthorizedException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(pin, account.getPin())) {
		log.info("<<< Exiting validatePin(accountNumber={},pin=<redacted>)", accountNumber);
            throw new UnauthorizedException(ApiMessages.PIN_INVALID_ERROR.getMessage());
        }
    }

    private void validatePassword(String accountNumber, String password) {
        val account = accountRepository.findByAccountNumber(accountNumber);
		log.info(">>> Entering validatePassword(accountNumber={},password=<redacted>)", accountNumber);
        if (account == null) {
			log.debug("Throwing NotFoundException from validatePassword(accountNumber={},password=<redacted>)", accountNumber);
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }
		log.info("validatePassword(accountNumber,password)={},<redacted>: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
		log.error("validatePassword(accountNumber={},password=<redacted>): account → {}", accountNumber, account);

        if (password == null || password.isEmpty()) {
			log.debug("Throwing UnauthorizedException from validatePassword(accountNumber={},password=<redacted>)", accountNumber);
            throw new UnauthorizedException(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
		log.info("<<< Exiting validatePassword(accountNumber={},password=<redacted>)", accountNumber);
            throw new UnauthorizedException(ApiMessages.PASSWORD_INVALID_ERROR.getMessage());
        }
    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {
        validatePassword(accountNumber, password);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account.getPin() != null) {
			log.debug("Throwing UnauthorizedException from createPin(accountNumber={},password=<redacted>,pin=<redacted>)", accountNumber);
			log.info("createPin(accountNumber,password,pin)={},<redacted>,<redacted>: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
            throw new UnauthorizedException(ApiMessages.PIN_ALREADY_EXISTS.getMessage());
        }

        if (pin == null || pin.isEmpty()) {
			log.debug("Throwing InvalidPinException from createPin(accountNumber={},password=<redacted>,pin=<redacted>)", accountNumber);
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!pin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

		log.info("createPin(accountNumber,password,pin)={},<redacted>,<redacted>: save query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
        account.setPin(passwordEncoder.encode(pin));
        accountRepository.save(account);
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
        log.info("Updating PIN for account: {}", accountNumber);

        validatePassword(accountNumber, password);
        validatePin(accountNumber, oldPin);

		long start = System.currentTimeMillis();
		log.info("updatePin(accountNumber,oldPin,password,newPin)={},<redacted>,<redacted>,<redacted>: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
        val account = accountRepository.findByAccountNumber(accountNumber);

        if (newPin == null || newPin.isEmpty()) {
			log.debug("Throwing InvalidPinException from updatePin(accountNumber={},oldPin=<redacted>,password=<redacted>,newPin=<redacted>)", accountNumber);
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!newPin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

		log.info("updatePin(accountNumber,oldPin,password,newPin)={},<redacted>,<redacted>,<redacted>: save query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
		log.error("updatePin(accountNumber={},oldPin=<redacted>,password=<redacted>,newPin=<redacted>): account → {}", accountNumber, account);
        account.setPin(passwordEncoder.encode(newPin));
        accountRepository.save(account);
		log.info("<<< Exiting updatePin(accountNumber={},oldPin=<redacted>,password=<redacted>,newPin=<redacted>)", accountNumber);
    }

    private void validateAmount(double amount) {
		log.info(">>> Entering validateAmount(amount={})", amount);
        if (amount <= 0) {
			log.debug("Throwing InvalidAmountException from validateAmount(amount={})", amount);
            throw new InvalidAmountException(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage());
        }

        if (amount % 100 != 0) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_NOT_MULTIPLE_OF_100_ERROR.getMessage());
        }

        if (amount > 100000) {
		log.info("<<< Exiting validateAmount(amount={})", amount);
            throw new InvalidAmountException(ApiMessages.AMOUNT_EXCEED_100_000_ERROR.getMessage());
        }
    }

    @Transactional
    @Override
    public void cashDeposit(String accountNumber, String pin, double amount) {
		log.info(">>> Entering cashDeposit(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
        validatePin(accountNumber, pin);
        validateAmount(amount);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
        val newBalance = currentBalance + amount;
		log.error("cashDeposit(accountNumber={},pin=<redacted>,amount={}): currentBalance → {}", accountNumber, amount, currentBalance);
		log.error("cashDeposit(accountNumber={},pin=<redacted>,amount={}): account → {}", accountNumber, amount, account);
		log.info("cashDeposit(accountNumber,pin,amount)={},<redacted>,{}: find query executed in {} ms", accountNumber, amount, (System.currentTimeMillis() - start));
		log.info("cashDeposit(accountNumber,pin,amount)={},<redacted>,{}: save query executed in {} ms", accountNumber, amount, (System.currentTimeMillis() - start));
        account.setBalance(newBalance);
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.error("cashDeposit(accountNumber={},pin=<redacted>,amount={}): transaction → {}", accountNumber, amount, transaction);
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
		log.info("<<< Exiting cashDeposit(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
    }

    @Transactional
    @Override
    public void cashWithdrawal(String accountNumber, String pin, double amount) {
		log.info(">>> Entering cashWithdrawal(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
        validatePin(accountNumber, pin);
        validateAmount(amount);

		long start = System.currentTimeMillis();
        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
        if (currentBalance < amount) {
			log.debug("Throwing InsufficientBalanceException from cashWithdrawal(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
			log.info("cashWithdrawal(accountNumber,pin,amount)={},<redacted>,{}: find query executed in {} ms", accountNumber, amount, (System.currentTimeMillis() - start));
			log.error("cashWithdrawal(accountNumber={},pin=<redacted>,amount={}): currentBalance → {}", accountNumber, amount, currentBalance);
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newBalance = currentBalance - amount;
        account.setBalance(newBalance);
		log.error("cashWithdrawal(accountNumber={},pin=<redacted>,amount={}): account → {}", accountNumber, amount, account);
		log.info("cashWithdrawal(accountNumber,pin,amount)={},<redacted>,{}: save query executed in {} ms", accountNumber, amount, (System.currentTimeMillis() - start));
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.error("cashWithdrawal(accountNumber={},pin=<redacted>,amount={}): transaction → {}", accountNumber, amount, transaction);
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
		log.info("<<< Exiting cashWithdrawal(accountNumber={},pin=<redacted>,amount={})", accountNumber, amount);
    }

    @Transactional
    @Override
    public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, double amount) {
		log.info(">>> Entering fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
        validatePin(sourceAccountNumber, pin);
        validateAmount(amount);

        if (sourceAccountNumber.equals(targetAccountNumber)) {
			log.debug("Throwing FundTransferException from fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
            throw new FundTransferException(ApiMessages.CASH_TRANSFER_SAME_ACCOUNT_ERROR.getMessage());
        }

		long start = System.currentTimeMillis();
        val targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);
        if (targetAccount == null) {
			log.debug("Throwing NotFoundException from fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
			log.info("fundTransfer(sourceAccountNumber,targetAccountNumber,pin,amount)={},{},<redacted>,{}: find query executed in {} ms", sourceAccountNumber, targetAccountNumber, amount, (System.currentTimeMillis() - start));
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
        val sourceBalance = sourceAccount.getBalance();
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): sourceBalance → {}", sourceAccountNumber, targetAccountNumber, amount, sourceBalance);
        if (sourceBalance < amount) {
			log.debug("Throwing InsufficientBalanceException from fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newSourceBalance = sourceBalance - amount;
        sourceAccount.setBalance(newSourceBalance);
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): sourceAccount → {}", sourceAccountNumber, targetAccountNumber, amount, sourceAccount);
		log.info("fundTransfer(sourceAccountNumber,targetAccountNumber,pin,amount)={},{},<redacted>,{}: save query executed in {} ms", sourceAccountNumber, targetAccountNumber, amount, (System.currentTimeMillis() - start));
        accountRepository.save(sourceAccount);

        val targetBalance = targetAccount.getBalance();
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): targetBalance → {}", sourceAccountNumber, targetAccountNumber, amount, targetBalance);
        val newTargetBalance = targetBalance + amount;
        targetAccount.setBalance(newTargetBalance);
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): targetAccount → {}", sourceAccountNumber, targetAccountNumber, amount, targetAccount);
        accountRepository.save(targetAccount);

        val transaction = new Transaction();
        transaction.setAmount(amount);
		log.error("fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={}): transaction → {}", sourceAccountNumber, targetAccountNumber, amount, transaction);
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transactionRepository.save(transaction);
		log.info("<<< Exiting fundTransfer(sourceAccountNumber={},targetAccountNumber={},pin=<redacted>,amount={})", sourceAccountNumber, targetAccountNumber, amount);
    }

}
