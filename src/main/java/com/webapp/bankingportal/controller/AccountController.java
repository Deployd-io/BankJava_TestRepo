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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/account")
@Slf4j
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

	log.debug(">>> Entering checkAccountPIN()");
    @GetMapping("/pin/check")
	log.debug("checkAccountPIN(): isPINValid → {}", isPINValid);
    public ResponseEntity<String> checkAccountPIN() {
		log.debug(">>> Entering checkAccountPIN()");
        val isPINValid = accountService.isPinCreated(LoggedinUser.getAccountNumber());
		log.debug("checkAccountPIN(): isPINValid → {}", isPINValid);
		log.debug("<<< Exiting checkAccountPIN()");
        val response = isPINValid ? ApiMessages.PIN_CREATED.getMessage()
                : ApiMessages.PIN_NOT_CREATED.getMessage();

		log.debug("<<< Exiting checkAccountPIN()");
		log.debug(">>> Entering createPIN(pinRequest={})", pinRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pin/create")
    public ResponseEntity<String> createPIN(@RequestBody PinRequest pinRequest) {
		log.debug(">>> Entering createPIN(pinRequest={})", pinRequest);
		log.debug("<<< Exiting createPIN(pinRequest={})", pinRequest);
        accountService.createPin(
                LoggedinUser.getAccountNumber(),
                pinRequest.password(),
                pinRequest.pin());
				log.debug(">>> Entering updatePIN(pinUpdateRequest={})", pinUpdateRequest);

		log.debug("<<< Exiting createPIN(pinRequest={})", pinRequest);
        return ResponseEntity.ok(ApiMessages.PIN_CREATION_SUCCESS.getMessage());
    }

    @PostMapping("/pin/update")
    public ResponseEntity<String> updatePIN(@RequestBody PinUpdateRequest pinUpdateRequest) {
		log.debug("<<< Exiting updatePIN(pinUpdateRequest={})", pinUpdateRequest);
		log.debug(">>> Entering updatePIN(pinUpdateRequest={})", pinUpdateRequest);
        accountService.updatePin(
                LoggedinUser.getAccountNumber(),
                pinUpdateRequest.oldPin(),
                pinUpdateRequest.password(),
                pinUpdateRequest.newPin());
				log.debug(">>> Entering cashDeposit(amountRequest={})", amountRequest);

		log.debug("<<< Exiting updatePIN(pinUpdateRequest={})", pinUpdateRequest);
        return ResponseEntity.ok(ApiMessages.PIN_UPDATE_SUCCESS.getMessage());
    }
	log.debug("<<< Exiting cashDeposit(amountRequest={})", amountRequest);

    @PostMapping("/deposit")
    public ResponseEntity<String> cashDeposit(@RequestBody AmountRequest amountRequest) {
		log.debug(">>> Entering cashDeposit(amountRequest={})", amountRequest);
		log.debug(">>> Entering cashWithdrawal(amountRequest={})", amountRequest);
        accountService.cashDeposit(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

		log.debug("<<< Exiting cashDeposit(amountRequest={})", amountRequest);
		log.debug("<<< Exiting cashWithdrawal(amountRequest={})", amountRequest);
        return ResponseEntity.ok(ApiMessages.CASH_DEPOSIT_SUCCESS.getMessage());
    }

    @PostMapping("/withdraw")
	log.debug(">>> Entering fundTransfer(fundTransferRequest={})", fundTransferRequest);
    public ResponseEntity<String> cashWithdrawal(@RequestBody AmountRequest amountRequest) {
		log.debug(">>> Entering cashWithdrawal(amountRequest={})", amountRequest);
        accountService.cashWithdrawal(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

		log.debug("<<< Exiting fundTransfer(fundTransferRequest={})", fundTransferRequest);
		log.debug("<<< Exiting cashWithdrawal(amountRequest={})", amountRequest);
        return ResponseEntity.ok(ApiMessages.CASH_WITHDRAWAL_SUCCESS.getMessage());
    }

	log.debug(">>> Entering getAllTransactionsByAccountNumber()");
    @PostMapping("/fund-transfer")
    public ResponseEntity<String> fundTransfer(@RequestBody FundTransferRequest fundTransferRequest) {
		log.debug(">>> Entering fundTransfer(fundTransferRequest={})", fundTransferRequest);
		log.debug("<<< Exiting getAllTransactionsByAccountNumber()");
        accountService.fundTransfer(
                LoggedinUser.getAccountNumber(),
                fundTransferRequest.targetAccountNumber(),
                fundTransferRequest.pin(),
                fundTransferRequest.amount());

		log.debug("<<< Exiting fundTransfer(fundTransferRequest={})", fundTransferRequest);
        return ResponseEntity.ok(ApiMessages.CASH_TRANSFER_SUCCESS.getMessage());
    }

    @GetMapping("/transactions")
    public ResponseEntity<String> getAllTransactionsByAccountNumber() {
		log.debug(">>> Entering getAllTransactionsByAccountNumber()");
        val transactions = transactionService
                .getAllTransactionsByAccountNumber(LoggedinUser.getAccountNumber());
		log.debug("<<< Exiting getAllTransactionsByAccountNumber()");
        return ResponseEntity.ok(JsonUtil.toJson(transactions));
    }

}
