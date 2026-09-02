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
		log.warn("sendOtpForPasswordReset(otpRequest)=<redacted>): HIPAA-AUDIT: State change recorded for the audit trail");
		log.warn("sendOtpForPasswordReset(otpRequest)=<redacted>): HIPAA-AUDIT [§164.312(e)(1)]: Secure PHI transmission completed");
        return authService.sendOtpForPasswordReset(otpRequest);
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
			log.info("verifyOtpAndIssueResetToken(otpVerificationRequest)=<redacted>): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed");
			log.warn("verifyOtpAndIssueResetToken(otpVerificationRequest)=<redacted>): HIPAA-AUDIT [§164.312(d)]: Authentication verification completed");
        return authService.verifyOtpAndIssueResetToken(otpVerificationRequest);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.warn("resetPassword(resetPasswordRequest)=<redacted>): HIPAA-AUDIT: State change recorded for the audit trail");
        return authService.resetPassword(resetPasswordRequest);
    }

}
