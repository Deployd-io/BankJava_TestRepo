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

@ControllerAdvice
public class GlobalExceptionHandler {
public static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountDoesNotExistException.class)
    public ResponseEntity<String> handleAccountDoesNotExistException(
            AccountDoesNotExistException ex) {
			log.info(">>> Entering handleAccountDoesNotExistException(ex={})", ex);
		log.info("<<< Exiting handleAccountDoesNotExistException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
		log.info(">>> Entering handleBadCredentialsException(ex={})", ex);
		log.info("<<< Exiting handleBadCredentialsException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(FundTransferException.class)
    public ResponseEntity<String> handleFundTransferException(FundTransferException ex) {
		log.info(">>> Entering handleFundTransferException(ex={})", ex);
		log.info("<<< Exiting handleFundTransferException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(GeolocationException.class)
    public ResponseEntity<String> handleGeolocationException(GeolocationException ex) {
		log.info(">>> Entering handleGeolocationException(ex={})", ex);
		log.info("<<< Exiting handleGeolocationException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException ex) {
			log.info(">>> Entering handleIllegalArgumentException(ex={})", ex);
		log.info("<<< Exiting handleIllegalArgumentException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(
            InsufficientBalanceException ex) {
			log.info(">>> Entering handleInsufficientBalanceException(ex={})", ex);
		log.info("<<< Exiting handleInsufficientBalanceException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<String> handleInvalidAmountException(InvalidAmountException ex) {
		log.info(">>> Entering handleInvalidAmountException(ex={})", ex);
		log.info("<<< Exiting handleInvalidAmountException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<String> handleInvalidOtpException(InvalidOtpException ex) {
		log.info(">>> Entering handleInvalidOtpException(ex={})", ex);
		log.info("<<< Exiting handleInvalidOtpException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<String> handleInvalidPinException(InvalidPinException ex) {
		log.info(">>> Entering handleInvalidPinException(ex={})", ex);
		log.info("<<< Exiting handleInvalidPinException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
		log.info(">>> Entering handleInvalidTokenException(ex={})", ex);
		log.info("<<< Exiting handleInvalidTokenException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
		log.info(">>> Entering handleJsonProcessingException(ex={})", ex);
		log.info("<<< Exiting handleJsonProcessingException(ex={})", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
		log.info(">>> Entering handleNotFoundException(ex={})", ex);
		log.info("<<< Exiting handleNotFoundException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OtpRetryLimitExceededException.class)
    public ResponseEntity<String> handleOtpRetryLimitExceededException(
            OtpRetryLimitExceededException ex) {
			log.info(">>> Entering handleOtpRetryLimitExceededException(ex={})", ex);
		log.info("<<< Exiting handleOtpRetryLimitExceededException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<String> handlePasswordResetException(PasswordResetException ex, WebRequest request) {
		log.info(">>> Entering handlePasswordResetException(ex={},request={})", ex, request);
		log.info("<<< Exiting handlePasswordResetException(ex={},request={})", ex, request);
        return ResponseEntity.internalServerError()
                .body("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClientException(RestClientException ex) {
		log.info(">>> Entering handleRestClientException(ex={})", ex);
		log.info("<<< Exiting handleRestClientException(ex={})", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
		log.info(">>> Entering handleUnauthorizedException(ex={})", ex);
		log.info("<<< Exiting handleUnauthorizedException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(UserInvalidException.class)
    public ResponseEntity<String> handleUserInvalidException(UserInvalidException ex) {
		log.info(">>> Entering handleUserInvalidException(ex={})", ex);
		log.info("<<< Exiting handleUserInvalidException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
		log.info(">>> Entering handleGlobalException(ex={},request={})", ex, request);
		log.info("<<< Exiting handleGlobalException(ex={},request={})", ex, request);
        return ResponseEntity.internalServerError()
                .body("{\"message\": \"An unexpected error occurred: " + ex.getMessage() + "\"}");
    }
}
