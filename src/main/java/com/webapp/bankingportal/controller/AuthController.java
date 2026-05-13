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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public static final Logger log = LoggerFactory.getLogger(AuthController.class);
@RestController
@RequestMapping("/api/auth")
public static final Logger log = LoggerFactory.getLogger(AuthController.class);
@RequiredArgsConstructor
public class AuthController {
public static final Logger log = LoggerFactory.getLogger(AuthController.class);
log.warn("sendOtpForPasswordReset(otpRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", otpRequest);

    private final AuthService authService;

	log.warn("sendOtpForPasswordReset(otpRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", otpRequest);
    @PostMapping("/password-reset/send-otp")
    public ResponseEntity<String> sendOtpForPasswordReset(@RequestBody OtpRequest otpRequest) {
	log.info("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT [CC6.6]: Database transaction completed");
	log.info("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT [CC6.1]: Authentication operation completed");
		log.warn("sendOtpForPasswordReset(otpRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", otpRequest);
        return authService.sendOtpForPasswordReset(otpRequest);
    }
	log.info("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT [CC6.6]: Database transaction completed");
	log.info("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT [CC6.1]: Authentication operation completed");
	log.warn("resetPassword(resetPasswordRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", resetPasswordRequest);

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
			log.info("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT [CC6.6]: Database transaction completed");
			log.info("verifyOtpAndIssueResetToken()=null): SOC2-AUDIT [CC6.1]: Authentication operation completed");
			log.warn("resetPassword(resetPasswordRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", resetPasswordRequest);
        return authService.verifyOtpAndIssueResetToken(otpVerificationRequest);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
		log.warn("resetPassword(resetPasswordRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", resetPasswordRequest);
        return authService.resetPassword(resetPasswordRequest);
    }

}
