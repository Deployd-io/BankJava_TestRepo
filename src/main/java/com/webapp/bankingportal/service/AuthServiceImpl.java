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
		log.error(">>> Entering generatePasswordResetToken(user={})", user);
        long start = System.currentTimeMillis();
        val existingToken = passwordResetTokenRepository.findByUser(user);
        if (isExistingTokenValid(existingToken)) {
            return existingToken.getToken();
        }
		log.error("generatePasswordResetToken(user={}): existingToken → {}", user, existingToken);

        val token = UUID.randomUUID().toString();
		log.error("generatePasswordResetToken(user={}): token → {}", user, token);
        val expiryDateTime = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
		log.error("generatePasswordResetToken(user={}): expiryDateTime → {}", user, expiryDateTime);
        val resetToken = new PasswordResetToken(token, user, expiryDateTime);
		log.error("generatePasswordResetToken(user={}): resetToken → {}", user, resetToken);
        passwordResetTokenRepository.save(resetToken);

		log.warn("<<< Exiting generatePasswordResetToken(user={})", user);
        return token;
    }

    @Override
    public boolean verifyPasswordResetToken(String token, User user) {
		log.error(">>> Entering verifyPasswordResetToken(token=<redacted>,user={})", user);
		log.warn("<<< Exiting verifyPasswordResetToken(token=<redacted>,user={})", user);
        return passwordResetTokenRepository.findByToken(token)
                .map(resetToken -> {
                    deletePasswordResetToken(token);
                    return user.equals(resetToken.getUser()) && resetToken.isTokenValid();
                })
                .orElse(false);
    }

    @Override
    public void deletePasswordResetToken(String token) {
		log.error(">>> Entering deletePasswordResetToken(token=<redacted>)");
        passwordResetTokenRepository.deleteByToken(token);
		log.warn("<<< Exiting deletePasswordResetToken(token=<redacted>)");
    }

    @Override
    public ResponseEntity<String> sendOtpForPasswordReset(OtpRequest otpRequest) {
		log.error(">>> Entering sendOtpForPasswordReset(otpRequest=<redacted>)");
        log.info("Received OTP request for identifier: {}", otpRequest.identifier());
        val user = userService.getUserByIdentifier(otpRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
        val generatedOtp = otpService.generateOTP(accountNumber);

        ResponseEntity<String> result = sendOtpEmail(user, accountNumber, generatedOtp);
		log.warn("<<< Exiting sendOtpForPasswordReset(otpRequest=<redacted>)");
        return result;
    }

    @Override
    public ResponseEntity<String> verifyOtpAndIssueResetToken(OtpVerificationRequest otpVerificationRequest) {
		log.error(">>> Entering verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>)");
        validateOtpRequest(otpVerificationRequest);
        val user = userService.getUserByIdentifier(otpVerificationRequest.identifier());
		log.error("verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>): user → {}", user);
        val accountNumber = user.getAccount().getAccountNumber();
		log.error("verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>): accountNumber → {}", accountNumber);

        if (!otpService.validateOTP(accountNumber, otpVerificationRequest.otp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }

        String resetToken = generatePasswordResetToken(user);
		log.error("verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>): resetToken → {}", resetToken);
        ResponseEntity<String> result = ResponseEntity.ok(String.format(ApiMessages.PASSWORD_RESET_TOKEN_ISSUED.getMessage(), resetToken));
		log.warn("<<< Exiting verifyOtpAndIssueResetToken(otpVerificationRequest=<redacted>)");
        return result;
    }

    @Override
    @Transactional
    public ResponseEntity<String> resetPassword(ResetPasswordRequest resetPasswordRequest) {
		log.error(">>> Entering resetPassword(resetPasswordRequest=<redacted>)");
        val user = userService.getUserByIdentifier(resetPasswordRequest.identifier());

		log.error("resetPassword(resetPasswordRequest=<redacted>): user → {}", user);
        if (!verifyPasswordResetToken(resetPasswordRequest.resetToken(), user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.TOKEN_INVALID_ERROR.getMessage());
        }

        try {
            boolean passwordResetSuccessful = userService.resetPassword(user, resetPasswordRequest.newPassword());
			log.error("resetPassword(resetPasswordRequest=<redacted>): passwordResetSuccessful → {}", passwordResetSuccessful);
            if (passwordResetSuccessful) {
                return ResponseEntity.ok(ApiMessages.PASSWORD_RESET_SUCCESS.getMessage());
            } else {
                return ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
            }
        } catch (Exception e) {
            log.error("Error resetting password for user: {}", user.getId(), e);
            ResponseEntity<String> result = ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
			log.warn("<<< Exiting resetPassword(resetPasswordRequest=<redacted>)");
            return result;
        }
    }

    private boolean isExistingTokenValid(PasswordResetToken existingToken) {
		log.error(">>> Entering isExistingTokenValid(existingToken=<redacted>)");
        boolean result = existingToken != null && existingToken.getExpiryDateTime().isAfter(LocalDateTime.now().plusMinutes(5));
		log.warn("<<< Exiting isExistingTokenValid(existingToken=<redacted>)");
        return result;
    }

    private ResponseEntity<String> sendOtpEmail(User user, String accountNumber, String generatedOtp) {
		log.error(">>> Entering sendOtpEmail(user={},accountNumber={},generatedOtp=<redacted>)", user, accountNumber);
        val emailSendingFuture = otpService.sendOTPByEmail(user.getEmail(), user.getName(), accountNumber,
                generatedOtp);

        val successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        val failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

        ResponseEntity<String> logriteResult = emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
		log.warn("<<< Exiting sendOtpEmail(user={},accountNumber={},generatedOtp=<redacted>)", user, accountNumber);
        return logriteResult;
    }

    private void validateOtpRequest(OtpVerificationRequest otpVerificationRequest) {
		log.error(">>> Entering validateOtpRequest(otpVerificationRequest=<redacted>)");
        if (otpVerificationRequest.identifier() == null || otpVerificationRequest.identifier().isEmpty()) {
			log.error("Throwing IllegalArgumentException from validateOtpRequest(otpVerificationRequest=<redacted>)");
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (otpVerificationRequest.otp() == null || otpVerificationRequest.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
		log.warn("<<< Exiting validateOtpRequest(otpVerificationRequest=<redacted>)");
    }

}