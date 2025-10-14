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
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountDoesNotExistException.class)
    public ResponseEntity<String> handleAccountDoesNotExistException(
            AccountDoesNotExistException ex) {
		log.debug(">>> Entering handleAccountDoesNotExistException()");
		log.debug("<<< Exiting handleAccountDoesNotExistException()");
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
		log.debug(">>> Entering handleBadCredentialsException(ex={})", ex);
		log.debug("<<< Exiting handleBadCredentialsException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(FundTransferException.class)
    public ResponseEntity<String> handleFundTransferException(FundTransferException ex) {
		log.debug(">>> Entering handleFundTransferException(ex={})", ex);
		log.debug("<<< Exiting handleFundTransferException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(GeolocationException.class)
    public ResponseEntity<String> handleGeolocationException(GeolocationException ex) {
		log.debug(">>> Entering handleGeolocationException(ex={})", ex);
		log.debug("<<< Exiting handleGeolocationException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException ex) {
		log.debug(">>> Entering handleIllegalArgumentException()");
		log.debug("<<< Exiting handleIllegalArgumentException()");
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(
            InsufficientBalanceException ex) {
		log.debug(">>> Entering handleInsufficientBalanceException()");
		log.debug("<<< Exiting handleInsufficientBalanceException()");
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<String> handleInvalidAmountException(InvalidAmountException ex) {
		log.debug(">>> Entering handleInvalidAmountException(ex={})", ex);
		log.debug("<<< Exiting handleInvalidAmountException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<String> handleInvalidOtpException(InvalidOtpException ex) {
		log.debug(">>> Entering handleInvalidOtpException(ex={})", ex);
		log.debug("<<< Exiting handleInvalidOtpException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<String> handleInvalidPinException(InvalidPinException ex) {
		log.debug(">>> Entering handleInvalidPinException(ex={})", ex);
		log.debug("<<< Exiting handleInvalidPinException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
		log.debug(">>> Entering handleInvalidTokenException(ex={})", ex);
		log.debug("<<< Exiting handleInvalidTokenException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
		log.debug(">>> Entering handleJsonProcessingException(ex={})", ex);
		log.debug("<<< Exiting handleJsonProcessingException(ex={})", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
		log.debug(">>> Entering handleNotFoundException(ex={})", ex);
		log.debug("<<< Exiting handleNotFoundException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OtpRetryLimitExceededException.class)
    public ResponseEntity<String> handleOtpRetryLimitExceededException(
            OtpRetryLimitExceededException ex) {
		log.debug(">>> Entering handleOtpRetryLimitExceededException()");
		log.debug("<<< Exiting handleOtpRetryLimitExceededException()");
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<String> handlePasswordResetException(PasswordResetException ex, WebRequest request) {
        return ResponseEntity.internalServerError()
		log.debug(">>> Entering handlePasswordResetException(ex={},request={})", ex, request);
		log.debug("<<< Exiting handlePasswordResetException(ex={},request={})", ex, request);
                .body("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClientException(RestClientException ex) {
		log.debug(">>> Entering handleRestClientException(ex={})", ex);
		log.debug("<<< Exiting handleRestClientException(ex={})", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
		log.debug(">>> Entering handleUnauthorizedException(ex={})", ex);
		log.debug("<<< Exiting handleUnauthorizedException(ex={})", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(UserInvalidException.class)
    public ResponseEntity<String> handleUserInvalidException(UserInvalidException ex) {
		log.debug(">>> Entering handleUserInvalidException(ex={})", ex);
		log.debug("<<< Exiting handleUserInvalidException(ex={})", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        return ResponseEntity.internalServerError()
		log.debug(">>> Entering handleGlobalException(ex={},request={})", ex, request);
		log.debug("<<< Exiting handleGlobalException(ex={},request={})", ex, request);
                .body("{\"message\": \"An unexpected error occurred: " + ex.getMessage() + "\"}");
    }
}
