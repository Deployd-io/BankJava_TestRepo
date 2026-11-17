package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.dto.AmountRequest;
import com.webapp.bankingportal.dto.FundTransferRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.dto.PinUpdateRequest;
import com.webapp.bankingportal.service.AccountService;
import com.webapp.bankingportal.service.TransactionService;
import com.webapp.bankingportal.util.ApiMessages;
import com.webapp.bankingportal.util.JsonUtil;
import com.webapp.bankingportal.util.LoggedinUser;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/pin/check")
    public ResponseEntity<String> checkAccountPIN() {
		log.error(">>> Entering checkAccountPIN()");
        val isPINValid = accountService.isPinCreated(LoggedinUser.getAccountNumber());
        val response = isPINValid ? ApiMessages.PIN_CREATED.getMessage()
                : ApiMessages.PIN_NOT_CREATED.getMessage();
				log.error("checkAccountPIN(): isPINValid → <redacted>");

        ResponseEntity<String> result = ResponseEntity.ok(response);
		log.warn("<<< Exiting checkAccountPIN()");
        return result;
    }

    @PostMapping("/pin/create")
    public ResponseEntity<String> createPIN(@RequestBody PinRequest pinRequest) {
		log.error(">>> Entering createPIN(pinRequest={})", "[accountNumber=" + pinRequest.accountNumber() + "]");
        accountService.createPin(
                LoggedinUser.getAccountNumber(),
                pinRequest.password(),
                pinRequest.pin());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.PIN_CREATION_SUCCESS.getMessage());
		log.warn("<<< Exiting createPIN(pinRequest={})", "[accountNumber=" + pinRequest.accountNumber() + "]");
        return result;
    }

    @PostMapping("/pin/update")
    public ResponseEntity<String> updatePIN(@RequestBody PinUpdateRequest pinUpdateRequest) {
		log.error(">>> Entering updatePIN(pinUpdateRequest={})", "[accountNumber=" + pinUpdateRequest.accountNumber() + "]");
        accountService.updatePin(
                LoggedinUser.getAccountNumber(),
                pinUpdateRequest.oldPin(),
                pinUpdateRequest.password(),
                pinUpdateRequest.newPin());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.PIN_UPDATE_SUCCESS.getMessage());
		log.warn("<<< Exiting updatePIN(pinUpdateRequest={})", "[accountNumber=" + pinUpdateRequest.accountNumber() + "]");
        return result;
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> cashDeposit(@RequestBody AmountRequest amountRequest) {
		log.error(">>> Entering cashDeposit(amountRequest={})", "[accountNumber=" + amountRequest.accountNumber() + ", amount=" + amountRequest.amount() + "]");
        accountService.cashDeposit(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.CASH_DEPOSIT_SUCCESS.getMessage());
		log.warn("<<< Exiting cashDeposit(amountRequest={}) -> {}", "[accountNumber=" + amountRequest.accountNumber() + ", amount=" + amountRequest.amount() + "]", result);
        return result;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> cashWithdrawal(@RequestBody AmountRequest amountRequest) {
		log.error(">>> Entering cashWithdrawal(amountRequest={})", "[accountNumber=" + amountRequest.accountNumber() + ", amount=" + amountRequest.amount() + "]");
        accountService.cashWithdrawal(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.CASH_WITHDRAWAL_SUCCESS.getMessage());
		log.warn("<<< Exiting cashWithdrawal(amountRequest={}) -> {}", "[accountNumber=" + amountRequest.accountNumber() + ", amount=" + amountRequest.amount() + "]", result);
        return result;
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<String> fundTransfer(@RequestBody FundTransferRequest fundTransferRequest) {
		log.error(">>> Entering fundTransfer(fundTransferRequest={})", "[sourceAccountNumber=" + fundTransferRequest.sourceAccountNumber() + ", targetAccountNumber=" + fundTransferRequest.targetAccountNumber() + ", amount=" + fundTransferRequest.amount() + "]");
        accountService.fundTransfer(
                LoggedinUser.getAccountNumber(),
                fundTransferRequest.targetAccountNumber(),
                fundTransferRequest.pin(),
                fundTransferRequest.amount(),
                fundTransferRequest.remarks());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.CASH_TRANSFER_SUCCESS.getMessage());
		log.warn("<<< Exiting fundTransfer(fundTransferRequest={}) -> {}", "[sourceAccountNumber=" + fundTransferRequest.sourceAccountNumber() + ", targetAccountNumber=" + fundTransferRequest.targetAccountNumber() + ", amount=" + fundTransferRequest.amount() + "]", result);
        return result;
    }

    @GetMapping("/transactions")
    public ResponseEntity<String> getAllTransactionsByAccountNumber() {
		log.error(">>> Entering getAllTransactionsByAccountNumber()");
        val transactions = transactionService
                .getAllTransactionsByAccountNumber(LoggedinUser.getAccountNumber());
        ResponseEntity<String> result = ResponseEntity.ok(JsonUtil.toJson(transactions));
		log.warn("<<< Exiting getAllTransactionsByAccountNumber() -> {}", result);
        return result;
    }

}
