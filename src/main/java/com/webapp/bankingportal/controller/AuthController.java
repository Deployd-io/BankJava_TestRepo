package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.ResetPasswordRequest;
import com.webapp.bankingportal.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/password-reset/send-otp")
    public ResponseEntity<String> sendOtpForPasswordReset(@RequestBody OtpRequest otpRequest) {
		log.info(">>> Entering sendOtpForPasswordReset(otpRequest=<redacted>)");
        ResponseEntity<String> result = authService.sendOtpForPasswordReset(otpRequest);
		log.info("<<< Exiting sendOtpForPasswordReset(otpRequest=<redacted>)");
        return result;
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
			log.info(">>> Entering verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>)");
        ResponseEntity<String> result = authService.verifyOtpAndIssueResetToken(otpVerificationRequest);
		log.info("<<< Exiting verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>)");
        return result;
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.info(">>> Entering resetPassword(resetPasswordRequest=<redacted>)");
        ResponseEntity<String> result = authService.resetPassword(resetPasswordRequest);
		log.info("<<< Exiting resetPassword(resetPasswordRequest=<redacted>)");
        return result;
    }

}
