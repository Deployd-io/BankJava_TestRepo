package com.webapp.bankingportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webapp.bankingportal.exception.AccountDoesNotExistException;
import com.webapp.bankingportal.exception.FundTransferException;
import com.webapp.bankingportal.exception.GeolocationException;
import com.webapp.bankingportal.exception.InsufficientBalanceException;
import com.webapp.bankingportal.exception.InvalidAmountException;
import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.exception.InvalidOtpException;
import com.webapp.bankingportal.exception.InvalidPinException;
import com.webapp.bankingportal.exception.NotFoundException;
import com.webapp.bankingportal.exception.OtpRetryLimitExceededException;
import com.webapp.bankingportal.exception.PasswordResetException;
import com.webapp.bankingportal.exception.UnauthorizedException;
import com.webapp.bankingportal.exception.UserInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
@ControllerAdvice
public class GlobalExceptionHandler {
public static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountDoesNotExistException.class)
    public ResponseEntity<String> handleAccountDoesNotExistException(
            AccountDoesNotExistException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

	log.info("handleBadCredentialsException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);
	log.warn("handleBadCredentialsException(ex)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", ex);
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
		log.info("handleBadCredentialsException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);
		log.warn("handleBadCredentialsException(ex)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(FundTransferException.class)
    public ResponseEntity<String> handleFundTransferException(FundTransferException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(GeolocationException.class)
    public ResponseEntity<String> handleGeolocationException(GeolocationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(
            InsufficientBalanceException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<String> handleInvalidAmountException(InvalidAmountException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
		log.info("handleInvalidOtpException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<String> handleInvalidOtpException(InvalidOtpException ex) {
		log.info("handleInvalidOtpException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<String> handleInvalidPinException(InvalidPinException ex) {
		log.info("handleInvalidTokenException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
		log.info("handleInvalidTokenException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
	log.info("handleNotFoundException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
		log.info("handleNotFoundException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OtpRetryLimitExceededException.class)
    public ResponseEntity<String> handleOtpRetryLimitExceededException(
            OtpRetryLimitExceededException ex) {
	log.warn("handlePasswordResetException(ex,request)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed", ex, request);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<String> handlePasswordResetException(PasswordResetException ex, WebRequest request) {
		log.warn("handlePasswordResetException(ex,request)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed", ex, request);
        return ResponseEntity.internalServerError()
                .body("{\"message\": \"" + ex.getMessage() + "\"}");
    }
	log.warn("handleUnauthorizedException(ex)={}): SOC2-AUDIT [CC6.3]: Authorization operation completed", ex);
	log.info("handleUnauthorizedException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClientException(RestClientException ex) {
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
		log.warn("handleUnauthorizedException(ex)={}): SOC2-AUDIT [CC6.3]: Authorization operation completed", ex);
		log.info("handleUnauthorizedException(ex)={}): SOC2-AUDIT [CC7.1]: System operation completed", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(UserInvalidException.class)
    public ResponseEntity<String> handleUserInvalidException(UserInvalidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        return ResponseEntity.internalServerError()
                .body("{\"message\": \"An unexpected error occurred: " + ex.getMessage() + "\"}");
    }
}
