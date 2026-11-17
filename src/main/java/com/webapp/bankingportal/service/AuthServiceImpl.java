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
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final int EXPIRATION_HOURS = 24;

    private final OtpService otpService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;

    @Override
    public String generatePasswordResetToken(User user) {
		log.error(">>> Entering generatePasswordResetToken(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        long start = System.currentTimeMillis();
        val existingToken = passwordResetTokenRepository.findByUser(user);
        if (isExistingTokenValid(existingToken)) {
            return existingToken.getToken();
        }
		log.error("generatePasswordResetToken(user)={}: find query executed in {} ms", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), (System.currentTimeMillis() - start));
		log.error("generatePasswordResetToken(user={}): existingToken → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));

        val token = UUID.randomUUID().toString();
		log.error("generatePasswordResetToken(user={}): token → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        val expiryDateTime = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
		log.error("generatePasswordResetToken(user={}): expiryDateTime → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), expiryDateTime);
        val resetToken = new PasswordResetToken(token, user, expiryDateTime);
		log.error("generatePasswordResetToken(user={}): resetToken → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        passwordResetTokenRepository.save(resetToken);
		log.error("generatePasswordResetToken(user)={}: save query executed in {} ms", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), (System.currentTimeMillis() - start));

		log.warn("<<< Exiting generatePasswordResetToken(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        return token;
    }

    @Override
    public boolean verifyPasswordResetToken(String token, User user) {
		log.error(">>> Entering verifyPasswordResetToken(token=<redacted>,user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
		log.warn("<<< Exiting verifyPasswordResetToken(token=<redacted>,user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
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
        long start = System.currentTimeMillis();
        passwordResetTokenRepository.deleteByToken(token);
		log.error("deletePasswordResetToken(token)=<redacted>: delete query executed in {} ms", (System.currentTimeMillis() - start));
    log.warn("<<< Exiting deletePasswordResetToken(token=<redacted>)");
    }

    @Override
    public ResponseEntity<String> sendOtpForPasswordReset(OtpRequest otpRequest) {
		log.error(">>> Entering sendOtpForPasswordReset(otpRequest={})", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        val user = userService.getUserByIdentifier(otpRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
		log.error("sendOtpForPasswordReset(otpRequest={}): accountNumber → {}", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"), accountNumber);
        val generatedOtp = otpService.generateOTP(accountNumber);
		log.error("sendOtpForPasswordReset(otpRequest={}): generatedOtp → <redacted>", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
		log.error("sendOtpForPasswordReset(otpRequest={}): user → <redacted>", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));

        ResponseEntity<String> result = sendOtpEmail(user, accountNumber, generatedOtp);
		log.warn("<<< Exiting sendOtpForPasswordReset(otpRequest={})", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        return result;
    }

    @Override
    public ResponseEntity<String> verifyOtpAndIssueResetToken(OtpVerificationRequest otpVerificationRequest) {
		log.error(">>> Entering verifyOtpAndIssueResetToken(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        validateOtpRequest(otpVerificationRequest);
        val user = userService.getUserByIdentifier(otpVerificationRequest.identifier());
		log.error("verifyOtpAndIssueResetToken(otpVerificationRequest={}): user → <redacted>", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        val accountNumber = user.getAccount().getAccountNumber();
		log.error("verifyOtpAndIssueResetToken(otpVerificationRequest={}): accountNumber → {}", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"), accountNumber);

        if (!otpService.validateOTP(accountNumber, otpVerificationRequest.otp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }

        String resetToken = generatePasswordResetToken(user);
		log.error("verifyOtpAndIssueResetToken(otpVerificationRequest={}): resetToken → <redacted>", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        ResponseEntity<String> result = ResponseEntity.ok(String.format(ApiMessages.PASSWORD_RESET_TOKEN_ISSUED.getMessage(), resetToken));
		log.warn("<<< Exiting verifyOtpAndIssueResetToken(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        return result;
    }

    @Override
    @Transactional
    public ResponseEntity<String> resetPassword(ResetPasswordRequest resetPasswordRequest) {
		log.error(">>> Entering resetPassword(resetPasswordRequest={})", (resetPasswordRequest == null ? "null" : "[identifier=" + resetPasswordRequest.identifier() + "]"));
        val user = userService.getUserByIdentifier(resetPasswordRequest.identifier());

		log.error("resetPassword(resetPasswordRequest={}): user → <redacted>", (resetPasswordRequest == null ? "null" : "[identifier=" + resetPasswordRequest.identifier() + "]"));
        if (!verifyPasswordResetToken(resetPasswordRequest.resetToken(), user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.TOKEN_INVALID_ERROR.getMessage());
        }

        try {
            boolean passwordResetSuccessful = userService.resetPassword(user, resetPasswordRequest.newPassword());
			log.error("resetPassword(resetPasswordRequest={}): passwordResetSuccessful → <redacted>", (resetPasswordRequest == null ? "null" : "[identifier=" + resetPasswordRequest.identifier() + "]"));
            if (passwordResetSuccessful) {
                return ResponseEntity.ok(ApiMessages.PASSWORD_RESET_SUCCESS.getMessage());
            } else {
                return ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
            }
        } catch (Exception e) {
			log.error("Exception in resetPassword(resetPasswordRequest={}): {}", (resetPasswordRequest == null ? "null" : "[identifier=" + resetPasswordRequest.identifier() + "]"), e.getMessage(), e);
            ResponseEntity<String> result = ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
			log.warn("<<< Exiting resetPassword(resetPasswordRequest={})", (resetPasswordRequest == null ? "null" : "[identifier=" + resetPasswordRequest.identifier() + "]"));
            return result;
        }
    }

    private boolean isExistingTokenValid(PasswordResetToken existingToken) {
		log.error(">>> Entering isExistingTokenValid(existingToken={})", (existingToken == null ? "null" : "[id=" + existingToken.getId() + ", expiryDateTime=" + existingToken.getExpiryDateTime() + "]"));
        boolean result = existingToken != null && existingToken.getExpiryDateTime().isAfter(LocalDateTime.now().plusMinutes(5));
		log.warn("<<< Exiting isExistingTokenValid(existingToken={})", (existingToken == null ? "null" : "[id=" + existingToken.getId() + ", expiryDateTime=" + existingToken.getExpiryDateTime() + "]"));
        return result;
    }

    private ResponseEntity<String> sendOtpEmail(User user, String accountNumber, String generatedOtp) {
		log.error(">>> Entering sendOtpEmail(user={},accountNumber={},generatedOtp=<redacted>)", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), accountNumber);
        val emailSendingFuture = otpService.sendOTPByEmail(user.getEmail(), user.getName(), accountNumber,
                generatedOtp);

        val successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        val failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

        ResponseEntity<String> logriteResult = emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
		log.warn("<<< Exiting sendOtpEmail(user={},accountNumber={},generatedOtp=<redacted>)", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), accountNumber);
        return logriteResult;
    }

    private void validateOtpRequest(OtpVerificationRequest otpVerificationRequest) {
		log.error(">>> Entering validateOtpRequest(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        if (otpVerificationRequest.identifier() == null || otpVerificationRequest.identifier().isEmpty()) {
			log.error("Throwing IllegalArgumentException from validateOtpRequest(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (otpVerificationRequest.otp() == null || otpVerificationRequest.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
		log.warn("<<< Exiting validateOtpRequest(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
    }

}