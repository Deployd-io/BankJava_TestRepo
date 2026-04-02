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
		log.warn("sendOtpForPasswordReset(otpRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", otpRequest);
        return authService.sendOtpForPasswordReset(otpRequest);
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
			log.warn("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT [CC6.1]: Password reset operation performed");
			log.info("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT [CC6.6]: Database transaction completed");
        return authService.verifyOtpAndIssueResetToken(otpVerificationRequest);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.warn("resetPassword(resetPasswordRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", resetPasswordRequest);
        return authService.resetPassword(resetPasswordRequest);
    }

}
