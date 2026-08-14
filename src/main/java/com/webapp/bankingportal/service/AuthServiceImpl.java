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
        if (isExistingTokenValid(existingToken)) {
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
        return passwordResetTokenRepository.findByToken(token)
                .map(resetToken -> {
                    deletePasswordResetToken(token);
                    return user.equals(resetToken.getUser()) && resetToken.isTokenValid();
                })
                .orElse(false);
    }

    @Override
    public void deletePasswordResetToken(String token) {
        passwordResetTokenRepository.deleteByToken(token);
    }

    @Override
    public ResponseEntity<String> sendOtpForPasswordReset(OtpRequest otpRequest) {
        log.info("Received OTP request for identifier: {}", otpRequest.identifier());
        val user = userService.getUserByIdentifier(otpRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
        val generatedOtp = otpService.generateOTP(accountNumber);

		log.warn("sendOtpForPasswordReset(otpRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", otpRequest, user);
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
		log.info("verifyOtpAndIssueResetToken(otpVerificationRequest)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", otpVerificationRequest);
		log.warn("verifyOtpAndIssueResetToken(otpVerificationRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", otpVerificationRequest, user);
		log.info("verifyOtpAndIssueResetToken(otpVerificationRequest)={}): SOC2-AUDIT [CC7.1]: System operation completed", otpVerificationRequest);
		log.info("verifyOtpAndIssueResetToken(otpVerificationRequest)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", otpVerificationRequest, user);

        String resetToken = generatePasswordResetToken(user);
        return ResponseEntity.ok(String.format(ApiMessages.PASSWORD_RESET_TOKEN_ISSUED.getMessage(), resetToken));
    }

    @Override
    @Transactional
    public ResponseEntity<String> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        val user = userService.getUserByIdentifier(resetPasswordRequest.identifier());

        if (!verifyPasswordResetToken(resetPasswordRequest.resetToken(), user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.TOKEN_INVALID_ERROR.getMessage());
        }

		log.warn("resetPassword(resetPasswordRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", resetPasswordRequest, user);
        try {
            boolean passwordResetSuccessful = userService.resetPassword(user, resetPasswordRequest.newPassword());
            if (passwordResetSuccessful) {
                return ResponseEntity.ok(ApiMessages.PASSWORD_RESET_SUCCESS.getMessage());
            } else {
                return ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
            }
        } catch (Exception e) {
            log.error("Error resetting password for user: {}", user.getId(), e);
			log.info("resetPassword(resetPasswordRequest)={}): SOC2-AUDIT [CC7.1]: System operation completed", resetPasswordRequest);
            return ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
        }
    }

    private boolean isExistingTokenValid(PasswordResetToken existingToken) {
        return existingToken != null && existingToken.getExpiryDateTime().isAfter(LocalDateTime.now().plusMinutes(5));
    }

    private ResponseEntity<String> sendOtpEmail(User user, String accountNumber, String generatedOtp) {
        val emailSendingFuture = otpService.sendOTPByEmail(user.getEmail(), user.getName(), accountNumber,
                generatedOtp);

        val successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        val failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

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
		log.info("validateOtpRequest(otpVerificationRequest)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", otpVerificationRequest);
    }

}