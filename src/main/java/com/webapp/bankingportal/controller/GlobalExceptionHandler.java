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
			log.debug(">>> Entering handleAccountDoesNotExistException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.debug("<<< Exiting handleAccountDoesNotExistException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
		log.debug(">>> Entering handleBadCredentialsException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		log.debug("<<< Exiting handleBadCredentialsException(ex={})", String.valueOf(ex));
        return result;
    }

    @ExceptionHandler(FundTransferException.class)
    public ResponseEntity<String> handleFundTransferException(FundTransferException ex) {
		log.debug(">>> Entering handleFundTransferException(ex={})", String.valueOf(ex));
        log.warn("CUSTOM-AUDIT: ENTERING: handleFundTransferException() method: username={}", (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() == null ? null : org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName()));
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.debug("<<< Exiting handleFundTransferException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(GeolocationException.class)
    public ResponseEntity<String> handleGeolocationException(GeolocationException ex) {
		log.debug(">>> Entering handleGeolocationException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.debug("<<< Exiting handleGeolocationException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException ex) {
			log.debug(">>> Entering handleIllegalArgumentException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.debug("<<< Exiting handleIllegalArgumentException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(
            InsufficientBalanceException ex) {
			log.debug(">>> Entering handleInsufficientBalanceException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.debug("<<< Exiting handleInsufficientBalanceException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<String> handleInvalidAmountException(InvalidAmountException ex) {
		log.debug(">>> Entering handleInvalidAmountException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.debug("<<< Exiting handleInvalidAmountException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<String> handleInvalidOtpException(InvalidOtpException ex) {
		log.debug(">>> Entering handleInvalidOtpException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		log.debug("<<< Exiting handleInvalidOtpException(ex={})", String.valueOf(ex));
        return result;
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<String> handleInvalidPinException(InvalidPinException ex) {
		log.debug(">>> Entering handleInvalidPinException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.debug("<<< Exiting handleInvalidPinException(ex={})", String.valueOf(ex));
        return result;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
		log.debug(">>> Entering handleInvalidTokenException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		log.debug("<<< Exiting handleInvalidTokenException(ex={})", String.valueOf(ex));
        return result;
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
		log.debug(">>> Entering handleJsonProcessingException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.internalServerError().body(ex.getMessage());
		log.debug("<<< Exiting handleJsonProcessingException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
		log.debug(">>> Entering handleNotFoundException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		log.debug("<<< Exiting handleNotFoundException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(OtpRetryLimitExceededException.class)
    public ResponseEntity<String> handleOtpRetryLimitExceededException(
            OtpRetryLimitExceededException ex) {
			log.debug(">>> Entering handleOtpRetryLimitExceededException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.debug("<<< Exiting handleOtpRetryLimitExceededException(ex={})", String.valueOf(ex));
        return result;
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<String> handlePasswordResetException(PasswordResetException ex, WebRequest request) {
		log.debug(">>> Entering handlePasswordResetException(ex={},request={})", String.valueOf(ex), request);
        ResponseEntity<String> result = ResponseEntity.internalServerError()
                .body("{\"message\": \"" + ex.getMessage() + "\"}");
		log.debug("<<< Exiting handlePasswordResetException(ex={},request={})", String.valueOf(ex), request);
        return result;
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleRestClientException(RestClientException ex) {
		log.debug(">>> Entering handleRestClientException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.internalServerError().body(ex.getMessage());
		log.debug("<<< Exiting handleRestClientException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
		log.debug(">>> Entering handleUnauthorizedException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		log.debug("<<< Exiting handleUnauthorizedException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(UserInvalidException.class)
    public ResponseEntity<String> handleUserInvalidException(UserInvalidException ex) {
		log.debug(">>> Entering handleUserInvalidException(ex={})", String.valueOf(ex));
        ResponseEntity<String> result = ResponseEntity.badRequest().body(ex.getMessage());
		log.debug("<<< Exiting handleUserInvalidException(ex={}) -> {}", String.valueOf(ex), result);
        return result;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
		log.debug(">>> Entering handleGlobalException(ex={},request={})", String.valueOf(ex), request);
        ResponseEntity<String> result = ResponseEntity.internalServerError()
                .body("{\"message\": \"An unexpected error occurred: " + ex.getMessage() + "\"}");
		log.debug("<<< Exiting handleGlobalException(ex={},request={}) -> {}", String.valueOf(ex), request, result);
        return result;
    }
}
