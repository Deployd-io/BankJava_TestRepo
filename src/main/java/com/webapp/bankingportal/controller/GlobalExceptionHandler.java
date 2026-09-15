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
			log.error(">>> Entering handleAccountDoesNotExistException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.warn("<<< Exiting handleAccountDoesNotExistException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
		log.error(">>> Entering handleBadCredentialsException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		log.warn("<<< Exiting handleBadCredentialsException(ex={})", ex);
        return result;
    }

    @ExceptionHandler(FundTransferException.class)
    public ResponseEntity<String> handleFundTransferException(FundTransferException ex) {
		log.error(">>> Entering handleFundTransferException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.warn("<<< Exiting handleFundTransferException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(GeolocationException.class)
    public ResponseEntity<String> handleGeolocationException(GeolocationException ex) {
		log.error(">>> Entering handleGeolocationException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.warn("<<< Exiting handleGeolocationException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException ex) {
			log.error(">>> Entering handleIllegalArgumentException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.warn("<<< Exiting handleIllegalArgumentException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(
            InsufficientBalanceException ex) {
			log.error(">>> Entering handleInsufficientBalanceException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.warn("<<< Exiting handleInsufficientBalanceException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<String> handleInvalidAmountException(InvalidAmountException ex) {
		log.error(">>> Entering handleInvalidAmountException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.warn("<<< Exiting handleInvalidAmountException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<String> handleInvalidOtpException(InvalidOtpException ex) {
		log.error(">>> Entering handleInvalidOtpException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		log.warn("<<< Exiting handleInvalidOtpException(ex={})", ex);
        return result;
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<String> handleInvalidPinException(InvalidPinException ex) {
		log.error(">>> Entering handleInvalidPinException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.warn("<<< Exiting handleInvalidPinException(ex={})", ex);
        return result;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
		log.error(">>> Entering handleInvalidTokenException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		log.warn("<<< Exiting handleInvalidTokenException(ex={})", ex);
        return result;
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
		log.error(">>> Entering handleJsonProcessingException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.internalServerError().body(ex.getMessage());
		log.warn("<<< Exiting handleJsonProcessingException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
		log.error(">>> Entering handleNotFoundException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		log.warn("<<< Exiting handleNotFoundException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(OtpRetryLimitExceededException.class)
    public ResponseEntity<String> handleOtpRetryLimitExceededException(
            OtpRetryLimitExceededException ex) {
			log.error(">>> Entering handleOtpRetryLimitExceededException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.warn("<<< Exiting handleOtpRetryLimitExceededException(ex={})", ex);
        return result;
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<String> handlePasswordResetException(PasswordResetException ex, WebRequest request) {
		log.error(">>> Entering handlePasswordResetException(ex={},request={})", ex, request);
        return ResponseEntity.internalServerError()
                .body("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClientException(RestClientException ex) {
		log.error(">>> Entering handleRestClientException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.internalServerError().body(ex.getMessage());
		log.warn("<<< Exiting handleRestClientException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
		log.error(">>> Entering handleUnauthorizedException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		log.warn("<<< Exiting handleUnauthorizedException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(UserInvalidException.class)
    public ResponseEntity<String> handleUserInvalidException(UserInvalidException ex) {
		log.error(">>> Entering handleUserInvalidException(ex={})", ex);
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.warn("<<< Exiting handleUserInvalidException(ex={}) -> {}", ex, result);
        return result;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
		log.error(">>> Entering handleGlobalException(ex={},request={})", ex, request);
        return ResponseEntity.internalServerError()
                .body("{\"message\": \"An unexpected error occurred: " + ex.getMessage() + "\"}");
    }
}
