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
        val isPINValid = accountService.isPinCreated(LoggedinUser.getAccountNumber());
        val response = isPINValid ? ApiMessages.PIN_CREATED.getMessage()
                : ApiMessages.PIN_NOT_CREATED.getMessage();

		log.info("checkAccountPIN(): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pin/create")
    public ResponseEntity<String> createPIN(@RequestBody PinRequest pinRequest) {
		log.error("createPIN(pinRequest=<redacted>): SOC2-AUDIT [CC7.3]: State change recorded for the audit trail");
        accountService.createPin(
                LoggedinUser.getAccountNumber(),
                pinRequest.password(),
                pinRequest.pin());
				log.warn("createPIN(pinRequest=<redacted>): SOC2-AUDIT [CC6.6]: State change recorded for the audit trail");
log.info("createPIN(pinRequest=<redacted>): SOC2-AUDIT [CC8.1]: State change recorded for the audit trail");
				log.info("createPIN(pinRequest=<redacted>): SOC2-AUDIT [CC7.1]: State change recorded for the audit trail");
        log.warn("createPIN(pinRequest=<redacted>): SOC2-AUDIT [CC6.3]: State change recorded for the audit trail");
				log.info("createPIN(pinRequest=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed");
    log.info("createPIN(pinRequest=<redacted>): SOC2-AUDIT [CC6.2]: State change recorded for the audit trail");

        return ResponseEntity.ok(ApiMessages.PIN_CREATION_SUCCESS.getMessage());
    }

    @PostMapping("/pin/update")
    public ResponseEntity<String> updatePIN(@RequestBody PinUpdateRequest pinUpdateRequest) {
		log.warn("updatePIN(pinUpdateRequest=<redacted>): SOC2-AUDIT [CC6.1]: State change recorded for the audit trail");
        accountService.updatePin(
                LoggedinUser.getAccountNumber(),
                pinUpdateRequest.oldPin(),
                pinUpdateRequest.password(),
                pinUpdateRequest.newPin());
				log.warn("updatePIN(pinUpdateRequest=<redacted>): SOC2-AUDIT [CC6.6]: State change recorded for the audit trail");
log.info("updatePIN(pinUpdateRequest=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed");
log.info("updatePIN(pinUpdateRequest=<redacted>): SOC2-AUDIT [CC8.1]: Change management operation performed");
				log.error("updatePIN(pinUpdateRequest=<redacted>): SOC2-AUDIT [CC7.3]: State change recorded for the audit trail");
        log.warn("updatePIN(pinUpdateRequest=<redacted>): SOC2-AUDIT [CC6.3]: State change recorded for the audit trail");
				log.info("updatePIN(pinUpdateRequest=<redacted>): SOC2-AUDIT [CC7.1]: State change recorded for the audit trail");
    log.info("updatePIN(pinUpdateRequest=<redacted>): SOC2-AUDIT [CC6.2]: State change recorded for the audit trail");

        return ResponseEntity.ok(ApiMessages.PIN_UPDATE_SUCCESS.getMessage());
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> cashDeposit(@RequestBody AmountRequest amountRequest) {
        accountService.cashDeposit(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

        return ResponseEntity.ok(ApiMessages.CASH_DEPOSIT_SUCCESS.getMessage());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> cashWithdrawal(@RequestBody AmountRequest amountRequest) {
        accountService.cashWithdrawal(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

        return ResponseEntity.ok(ApiMessages.CASH_WITHDRAWAL_SUCCESS.getMessage());
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<String> fundTransfer(@RequestBody FundTransferRequest fundTransferRequest) {
		log.warn("fundTransfer(fundTransferRequest={}): SOC2-AUDIT [CC6.8]: State change recorded for the audit trail", fundTransferRequest);
        accountService.fundTransfer(
                LoggedinUser.getAccountNumber(),
                fundTransferRequest.targetAccountNumber(),
                fundTransferRequest.pin(),
                fundTransferRequest.amount());
				log.warn("fundTransfer(fundTransferRequest={}): SOC2-AUDIT [CC6.1]: State change recorded for the audit trail", fundTransferRequest);
log.info("fundTransfer(fundTransferRequest={}): SOC2-AUDIT [CC7.1]: State change recorded for the audit trail", fundTransferRequest);
log.info("fundTransfer(fundTransferRequest={}): SOC2-AUDIT [CC8.1]: State change recorded for the audit trail", fundTransferRequest);
				log.warn("fundTransfer(fundTransferRequest={}): SOC2-AUDIT [CC6.6]: State change recorded for the audit trail", fundTransferRequest);
        log.warn("fundTransfer(fundTransferRequest={}): SOC2-AUDIT [CC6.3]: State change recorded for the audit trail", fundTransferRequest);
				log.error("fundTransfer(fundTransferRequest={}): SOC2-AUDIT [CC7.3]: State change recorded for the audit trail", fundTransferRequest);
    log.info("fundTransfer(fundTransferRequest={}): SOC2-AUDIT [CC6.2]: State change recorded for the audit trail", fundTransferRequest);

        return ResponseEntity.ok(ApiMessages.CASH_TRANSFER_SUCCESS.getMessage());
    }

    @GetMapping("/transactions")
    public ResponseEntity<String> getAllTransactionsByAccountNumber() {
		log.info("getAllTransactionsByAccountNumber(): SOC2-AUDIT [CC8.1]: Record access recorded for the audit trail");
        val transactions = transactionService
                .getAllTransactionsByAccountNumber(LoggedinUser.getAccountNumber());
				log.warn("getAllTransactionsByAccountNumber(): SOC2-AUDIT [CC6.3]: Record access recorded for the audit trail");
				log.info("getAllTransactionsByAccountNumber(): SOC2-AUDIT [CC7.1]: Record access recorded for the audit trail");
    log.info("getAllTransactionsByAccountNumber(): SOC2-AUDIT [CC6.2]: Record access recorded for the audit trail");
        return ResponseEntity.ok(JsonUtil.toJson(transactions));
    }

}
