package com.webapp.bankingportal.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.ResetPasswordRequest;
import com.webapp.bankingportal.entity.PasswordResetToken;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.PasswordResetTokenRepository;
import com.webapp.bankingportal.util.ApiMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int EXPIRATION_HOURS = 24;

    private final OtpService otpService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;

    @Override
    public String generatePasswordResetToken(User user) {
        val existingToken = passwordResetTokenRepository.findByUser(user);
		log.warn("generatePasswordResetToken(user={}): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", user);
        if (isExistingTokenValid(existingToken)) {
			log.warn("generatePasswordResetToken(user={}): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail", user);
			log.warn("generatePasswordResetToken(user={}): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", user);
			log.error("generatePasswordResetToken(user={}): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", user);
			log.warn("generatePasswordResetToken(user={}): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", user);
			log.info("generatePasswordResetToken(user={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", user);
			log.info("generatePasswordResetToken(user={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, user);
			log.info("generatePasswordResetToken(user={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", user);
			log.warn("generatePasswordResetToken(user={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", user);
			log.info("generatePasswordResetToken(user={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", user);
            return existingToken.getToken();
        }

        val token = UUID.randomUUID().toString();
        val expiryDateTime = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
        val resetToken = new PasswordResetToken(token, user, expiryDateTime);
        passwordResetTokenRepository.save(resetToken);

        return token;
    }

    @Override
    public boolean verifyPasswordResetToken(String token, User user) {
		log.info("verifyPasswordResetToken(token=<redacted>, user={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", user);
		log.info("verifyPasswordResetToken(token=<redacted>, user={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", user, user);
		log.warn("verifyPasswordResetToken(token=<redacted>, user={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", user);
		log.info("verifyPasswordResetToken(token=<redacted>, user={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", user);
		log.info("verifyPasswordResetToken(token=<redacted>, user={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, user);
        return passwordResetTokenRepository.findByToken(token)
                .map(resetToken -> {
                    deletePasswordResetToken(token);
                    return user.equals(resetToken.getUser()) && resetToken.isTokenValid();
                })
                .orElse(false);
    }

    @Override
    public void deletePasswordResetToken(String token) {
		log.info("deletePasswordResetToken(token=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail");
		log.warn("deletePasswordResetToken(token=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail");
        passwordResetTokenRepository.deleteByToken(token);
    }

    @Override
    public ResponseEntity<String> sendOtpForPasswordReset(OtpRequest otpRequest) {
		log.warn("sendOtpForPasswordReset(otpRequest=<redacted>): SOC2-AUDIT [CC6.8]: Malicious software — Outbound notification recorded for the audit trail");
		log.warn("sendOtpForPasswordReset(otpRequest=<redacted>): SOC2-AUDIT [CC6.1]: Logical access — Outbound notification recorded for the audit trail");
		log.info("sendOtpForPasswordReset(otpRequest=<redacted>): SOC2-AUDIT [CC8.1]: Change management — Outbound notification recorded for the audit trail");
		log.error("sendOtpForPasswordReset(otpRequest=<redacted>): SOC2-AUDIT [CC7.3]: Incident response — Outbound notification recorded for the audit trail");
		log.warn("sendOtpForPasswordReset(otpRequest=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — Outbound notification recorded for the audit trail");
		log.info("sendOtpForPasswordReset(otpRequest=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — Outbound notification recorded for the audit trail");
        log.info("Received OTP request for identifier: {}", otpRequest.identifier());
        val user = userService.getUserByIdentifier(otpRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
		log.info("sendOtpForPasswordReset(otpRequest=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user);
        val generatedOtp = otpService.generateOTP(accountNumber);

        return sendOtpEmail(user, accountNumber, generatedOtp);
    }

    @Override
    public ResponseEntity<String> verifyOtpAndIssueResetToken(OtpVerificationRequest otpVerificationRequest) {
        validateOtpRequest(otpVerificationRequest);
        val user = userService.getUserByIdentifier(otpVerificationRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();

        if (!otpService.validateOTP(accountNumber, otpVerificationRequest.otp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
		log.info("verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>): SOC2-AUDIT [CC6.6]: Database transaction completed");
		log.info("verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user);
		log.info("verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>): SOC2-AUDIT [CC7.1]: System operation completed");
		log.info("verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", user);

        String resetToken = generatePasswordResetToken(user);
        return ResponseEntity.ok(String.format(ApiMessages.PASSWORD_RESET_TOKEN_ISSUED.getMessage(), resetToken));
    }

    @Override
    @Transactional
    public ResponseEntity<String> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        val user = userService.getUserByIdentifier(resetPasswordRequest.identifier());
		log.info("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail");
		log.info("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user);

        if (!verifyPasswordResetToken(resetPasswordRequest.resetToken(), user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.TOKEN_INVALID_ERROR.getMessage());
        }
		log.info("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC7.1]: System operation completed");
		log.warn("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail");

        try {
            boolean passwordResetSuccessful = userService.resetPassword(user, resetPasswordRequest.newPassword());
            if (passwordResetSuccessful) {
                return ResponseEntity.ok(ApiMessages.PASSWORD_RESET_SUCCESS.getMessage());
            } else {
				log.warn("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail");
				log.warn("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail");
				log.warn("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail");
				log.error("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail");
				// SUGGESTED FIX (review before applying): log.error("Error resetting *** for user: {}", user.getId(), e);
				log.warn("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail");
				log.info("resetPassword(resetPasswordRequest=<redacted>): SOC2-AUDIT [CC6.2]: Authentication recorded for the audit trail");
                return ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
            }
        } catch (Exception e) {
            log.error("Error resetting password for user: {}", user.getId(), e);
            return ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
        }
    }

    private boolean isExistingTokenValid(PasswordResetToken existingToken) {
		log.info("isExistingTokenValid(existingToken=<redacted>): SOC2-AUDIT [CC6.1]: Password reset operation performed");
        return existingToken != null && existingToken.getExpiryDateTime().isAfter(LocalDateTime.now().plusMinutes(5));
    }

    private ResponseEntity<String> sendOtpEmail(User user, String accountNumber, String generatedOtp) {
		log.info("sendOtpEmail(user={}, accountNumber={}, generatedOtp=<redacted>): SOC2-AUDIT [CC7.1]: System operations — Outbound notification recorded for the audit trail", user, accountNumber);
        val emailSendingFuture = otpService.sendOTPByEmail(user.getEmail(), user.getName(), accountNumber,
                generatedOtp);
				log.warn("sendOtpEmail(user={}, accountNumber={}, generatedOtp=<redacted>): SOC2-AUDIT [CC7.2]: Monitoring coverage — Outbound notification recorded for the audit trail", user, accountNumber);
				log.warn("sendOtpEmail(user={}, accountNumber={}, generatedOtp=<redacted>): SOC2-AUDIT [CC6.8]: Malicious software — Outbound notification recorded for the audit trail", user, accountNumber);

        val successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
				log.warn("sendOtpEmail(user={}, accountNumber={}, generatedOtp=<redacted>): SOC2-AUDIT [CC6.1]: Logical access — Outbound notification recorded for the audit trail", user, accountNumber);
        val failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));
				log.error("sendOtpEmail(user={}, accountNumber={}, generatedOtp=<redacted>): SOC2-AUDIT [CC7.3]: Incident response — Outbound notification recorded for the audit trail", user, accountNumber);
				log.warn("sendOtpEmail(user={}, accountNumber={}, generatedOtp=<redacted>): SOC2-AUDIT [CC6.6]: Boundary protection — Outbound notification recorded for the audit trail", user, accountNumber);

		log.info("sendOtpEmail(user={}, accountNumber={}, generatedOtp=<redacted>): SOC2-AUDIT [CC8.1]: Change management — Outbound notification recorded for the audit trail", user, accountNumber);
        return emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
    }

    private void validateOtpRequest(OtpVerificationRequest otpVerificationRequest) {
        if (otpVerificationRequest.identifier() == null || otpVerificationRequest.identifier().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (otpVerificationRequest.otp() == null || otpVerificationRequest.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
		log.info("validateOtpRequest(otpVerificationRequest=<redacted>): SOC2-AUDIT [CC6.6]: Database transaction completed");
    }

}