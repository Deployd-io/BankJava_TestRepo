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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/pin/check")
    public ResponseEntity<String> checkAccountPIN() {
		log.debug(">>> Entering checkAccountPIN()");
        val isPINValid = accountService.isPinCreated(LoggedinUser.getAccountNumber());
        val response = isPINValid ? ApiMessages.PIN_CREATED.getMessage()
                : ApiMessages.PIN_NOT_CREATED.getMessage();
				log.debug("checkAccountPIN(): isPINValid → {}", isPINValid);

        ResponseEntity<String> result = ResponseEntity.ok(response);
		log.debug("<<< Exiting checkAccountPIN()");
        return result;
    }

    @PostMapping("/pin/create")
    public ResponseEntity<String> createPIN(@RequestBody PinRequest pinRequest) {
		log.debug(">>> Entering createPIN(pinRequest=<redacted>)");
        accountService.createPin(
                LoggedinUser.getAccountNumber(),
                pinRequest.password(),
                pinRequest.pin());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.PIN_CREATION_SUCCESS.getMessage());
		log.debug("<<< Exiting createPIN(pinRequest=<redacted>)");
        return result;
    }

    @PostMapping("/pin/update")
    public ResponseEntity<String> updatePIN(@RequestBody PinUpdateRequest pinUpdateRequest) {
		log.debug(">>> Entering updatePIN(pinUpdateRequest=<redacted>)");
        accountService.updatePin(
                LoggedinUser.getAccountNumber(),
                pinUpdateRequest.oldPin(),
                pinUpdateRequest.password(),
                pinUpdateRequest.newPin());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.PIN_UPDATE_SUCCESS.getMessage());
		log.debug("<<< Exiting updatePIN(pinUpdateRequest=<redacted>)");
        return result;
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> cashDeposit(@RequestBody AmountRequest amountRequest) {
		log.debug(">>> Entering cashDeposit(amountRequest={})", amountRequest);
        accountService.cashDeposit(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.CASH_DEPOSIT_SUCCESS.getMessage());
		log.debug("<<< Exiting cashDeposit(amountRequest={}) -> {}", amountRequest, result);
        return result;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> cashWithdrawal(@RequestBody AmountRequest amountRequest) {
		log.debug(">>> Entering cashWithdrawal(amountRequest={})", amountRequest);
        accountService.cashWithdrawal(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.CASH_WITHDRAWAL_SUCCESS.getMessage());
		log.debug("<<< Exiting cashWithdrawal(amountRequest={}) -> {}", amountRequest, result);
        return result;
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<String> fundTransfer(@RequestBody FundTransferRequest fundTransferRequest) {
		log.debug(">>> Entering fundTransfer(fundTransferRequest={})", fundTransferRequest);
        accountService.fundTransfer(
                LoggedinUser.getAccountNumber(),
                fundTransferRequest.targetAccountNumber(),
                fundTransferRequest.pin(),
                fundTransferRequest.amount());

        ResponseEntity<String> result = ResponseEntity.ok(ApiMessages.CASH_TRANSFER_SUCCESS.getMessage());
		log.debug("<<< Exiting fundTransfer(fundTransferRequest={}) -> {}", fundTransferRequest, result);
        return result;
    }

    @GetMapping("/transactions")
    public ResponseEntity<String> getAllTransactionsByAccountNumber() {
		log.debug(">>> Entering getAllTransactionsByAccountNumber()");
        val transactions = transactionService
                .getAllTransactionsByAccountNumber(LoggedinUser.getAccountNumber());
        ResponseEntity<String> result = ResponseEntity.ok(JsonUtil.toJson(transactions));
		log.debug("<<< Exiting getAllTransactionsByAccountNumber() -> {}", result);
        return result;
    }

}
