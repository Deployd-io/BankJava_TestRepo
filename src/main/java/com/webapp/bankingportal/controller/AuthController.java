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
		log.warn("sendOtpForPasswordReset(otpRequest)={}): SOC2-AUDIT: Password reset operation performed for user {}", otpRequest, user);
        return authService.sendOtpForPasswordReset(otpRequest);
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
			log.warn("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT: Authentication attempt completed for user {}", authResult);
		log.warn("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT: Password reset operation performed for user {}", user);
		log.info("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT: Database transaction completed with status {}", transactionStatus);
        return authService.verifyOtpAndIssueResetToken(otpVerificationRequest);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.warn("resetPassword(resetPasswordRequest)={}): SOC2-AUDIT: Password reset operation performed for user {}", resetPasswordRequest, user);
        return authService.resetPassword(resetPasswordRequest);
    }

}
