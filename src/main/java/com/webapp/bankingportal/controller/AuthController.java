package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.ResetPasswordRequest;
import com.webapp.bankingportal.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

	log.debug(">>> Entering sendOtpForPasswordReset(otpRequest={})", otpRequest);
    @PostMapping("/password-reset/send-otp")
	log.debug("<<< Exiting sendOtpForPasswordReset(otpRequest={})", otpRequest);
    public ResponseEntity<String> sendOtpForPasswordReset(@RequestBody OtpRequest otpRequest) {
		log.debug(">>> Entering sendOtpForPasswordReset(otpRequest={})", otpRequest);
		log.debug("<<< Exiting sendOtpForPasswordReset(otpRequest={})", otpRequest);
        return authService.sendOtpForPasswordReset(otpRequest);
    }

	log.debug("<<< Exiting verifyOtpAndIssueResetToken()");
    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
			log.debug(">>> Entering verifyOtpAndIssueResetToken()");
			log.debug(">>> Entering resetPassword(resetPasswordRequest={})", resetPasswordRequest);
		log.debug("<<< Exiting verifyOtpAndIssueResetToken()");
		log.debug("<<< Exiting resetPassword(resetPasswordRequest={})", resetPasswordRequest);
        return authService.verifyOtpAndIssueResetToken(otpVerificationRequest);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.debug(">>> Entering resetPassword(resetPasswordRequest={})", resetPasswordRequest);
		log.debug("<<< Exiting resetPassword(resetPasswordRequest={})", resetPasswordRequest);
        return authService.resetPassword(resetPasswordRequest);
    }

}
