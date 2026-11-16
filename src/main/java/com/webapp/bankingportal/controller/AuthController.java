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
		log.debug(">>> Entering sendOtpForPasswordReset(otpRequest={})", "[identifier=" + otpRequest.identifier() + "]");
        ResponseEntity<String> result = authService.sendOtpForPasswordReset(otpRequest);
		log.debug("<<< Exiting sendOtpForPasswordReset(otpRequest={})", "[identifier=" + otpRequest.identifier() + "]");
        return result;
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
			log.debug(">>> Entering verifyOtpAndIssueResetToken(otpVerificationRequest={})", "[identifier=" + otpVerificationRequest.identifier() + "]");
        ResponseEntity<String> result = authService.verifyOtpAndIssueResetToken(otpVerificationRequest);
		log.debug("<<< Exiting verifyOtpAndIssueResetToken(otpVerificationRequest={})", "[identifier=" + otpVerificationRequest.identifier() + "]");
        return result;
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.debug(">>> Entering resetPassword(resetPasswordRequest={})", "[identifier=" + resetPasswordRequest.identifier() + "]");
        ResponseEntity<String> result = authService.resetPassword(resetPasswordRequest);
		log.debug("<<< Exiting resetPassword(resetPasswordRequest={})", "[identifier=" + resetPasswordRequest.identifier() + "]");
        return result;
    }

}
