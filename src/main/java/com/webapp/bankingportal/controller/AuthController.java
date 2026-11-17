package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.ResetPasswordRequest;
import com.webapp.bankingportal.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/password-reset/send-otp")
    public ResponseEntity<String> sendOtpForPasswordReset(@RequestBody OtpRequest otpRequest) {
		log.error(">>> Entering sendOtpForPasswordReset(otpRequest={})", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        ResponseEntity<String> result = authService.sendOtpForPasswordReset(otpRequest);
		log.warn("<<< Exiting sendOtpForPasswordReset(otpRequest={})", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        return result;
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
			log.error(">>> Entering verifyOtpAndIssueResetToken(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        ResponseEntity<String> result = authService.verifyOtpAndIssueResetToken(otpVerificationRequest);
		log.warn("<<< Exiting verifyOtpAndIssueResetToken(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        return result;
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.error(">>> Entering resetPassword(resetPasswordRequest={})", (resetPasswordRequest == null ? "null" : "[identifier=" + resetPasswordRequest.identifier() + "]"));
        ResponseEntity<String> result = authService.resetPassword(resetPasswordRequest);
		log.warn("<<< Exiting resetPassword(resetPasswordRequest={})", (resetPasswordRequest == null ? "null" : "[identifier=" + resetPasswordRequest.identifier() + "]"));
        return result;
    }

}
