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
		log.debug(">>> Entering generatePasswordResetToken(user={})", user);
		log.debug("generatePasswordResetToken(user={}): existingToken → {}", user, existingToken);
		long start = System.currentTimeMillis();
		log.info("generatePasswordResetToken(user)={}: find query executed in {} ms", user, (System.currentTimeMillis() - start));
        if (isExistingTokenValid(existingToken)) {
			log.debug(">>> Entering isExistingTokenValid(existingToken={})", existingToken);
			log.debug("<<< Exiting isExistingTokenValid(existingToken={})", existingToken);
            return existingToken.getToken();
        }

        val token = UUID.randomUUID().toString();
		log.warn("generatePasswordResetToken(user={}): token → {}", user, token);
        val expiryDateTime = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
		log.debug("generatePasswordResetToken(user={}): expiryDateTime → {}", user, expiryDateTime);
        val resetToken = new PasswordResetToken(token, user, expiryDateTime);
		log.debug("generatePasswordResetToken(user={}): resetToken → {}", user, resetToken);
        passwordResetTokenRepository.save(resetToken);
		log.info("generatePasswordResetToken(user)={}: save query executed in {} ms", user, (System.currentTimeMillis() - start));

		log.debug("<<< Exiting generatePasswordResetToken(user={})", user);
        return token;
    }

    @Override
    public boolean verifyPasswordResetToken(String token, User user) {
        return passwordResetTokenRepository.findByToken(token)
		log.debug(">>> Entering verifyPasswordResetToken(token={},user={})", token, user);
		long start = System.currentTimeMillis();
                .map(resetToken -> {
                    deletePasswordResetToken(token);
					log.info("verifyPasswordResetToken(token,user)={},{}: find query executed in {} ms", token, user, (System.currentTimeMillis() - start));
					log.debug(">>> Entering deletePasswordResetToken(token={})", token);
					log.debug("<<< Exiting deletePasswordResetToken(token={})", token);
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
		log.debug(">>> Entering sendOtpForPasswordReset(otpRequest={})", otpRequest);
        val user = userService.getUserByIdentifier(otpRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
        val generatedOtp = otpService.generateOTP(accountNumber);

		log.debug("<<< Exiting sendOtpForPasswordReset(otpRequest={})", otpRequest);
        return sendOtpEmail(user, accountNumber, generatedOtp);
    }

    @Override
    public ResponseEntity<String> verifyOtpAndIssueResetToken(OtpVerificationRequest otpVerificationRequest) {
        validateOtpRequest(otpVerificationRequest);
		log.debug(">>> Entering verifyOtpAndIssueResetToken(otpVerificationRequest={})", otpVerificationRequest);
        val user = userService.getUserByIdentifier(otpVerificationRequest.identifier());
		log.debug("<<< Exiting verifyOtpAndIssueResetToken(otpVerificationRequest={})", otpVerificationRequest);
        val accountNumber = user.getAccount().getAccountNumber();

        if (!otpService.validateOTP(accountNumber, otpVerificationRequest.otp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }

        String resetToken = generatePasswordResetToken(user);
        return ResponseEntity.ok(String.format(ApiMessages.PASSWORD_RESET_TOKEN_ISSUED.getMessage(), resetToken));
    }

    @Override
    @Transactional
    public ResponseEntity<String> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        val user = userService.getUserByIdentifier(resetPasswordRequest.identifier());
		log.debug(">>> Entering resetPassword(resetPasswordRequest={})", resetPasswordRequest);
		log.debug("resetPassword(resetPasswordRequest={}): user → {}", resetPasswordRequest, user);

        if (!verifyPasswordResetToken(resetPasswordRequest.resetToken(), user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.TOKEN_INVALID_ERROR.getMessage());
			log.debug("<<< Exiting resetPassword(resetPasswordRequest={})", resetPasswordRequest);
        }

        try {
            boolean passwordResetSuccessful = userService.resetPassword(user, resetPasswordRequest.newPassword());
			log.debug("resetPassword(resetPasswordRequest={}): passwordResetSuccessful → {}", resetPasswordRequest, passwordResetSuccessful);
            if (passwordResetSuccessful) {
                return ResponseEntity.ok(ApiMessages.PASSWORD_RESET_SUCCESS.getMessage());
            } else {
                return ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
            }
        } catch (Exception e) {
            log.error("Error resetting password for user: {}", user.getId(), e);
            return ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
        }
    }

    private boolean isExistingTokenValid(PasswordResetToken existingToken) {
        return existingToken != null && existingToken.getExpiryDateTime().isAfter(LocalDateTime.now().plusMinutes(5));
    }

    private ResponseEntity<String> sendOtpEmail(User user, String accountNumber, String generatedOtp) {
        val emailSendingFuture = otpService.sendOTPByEmail(user.getEmail(), user.getName(), accountNumber,
                generatedOtp);
				log.debug(">>> Entering sendOtpEmail(user={},accountNumber={},generatedOtp={})", user, accountNumber, generatedOtp);

        val successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        val failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

        return emailSendingFuture.thenApply(result -> successResponse)
		log.debug("<<< Exiting sendOtpEmail(user={},accountNumber={},generatedOtp={})", user, accountNumber, generatedOtp);
                .exceptionally(e -> failureResponse).join();
    }

    private void validateOtpRequest(OtpVerificationRequest otpVerificationRequest) {
        if (otpVerificationRequest.identifier() == null || otpVerificationRequest.identifier().isEmpty()) {
			log.debug(">>> Entering validateOtpRequest(otpVerificationRequest={})", otpVerificationRequest);
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (otpVerificationRequest.otp() == null || otpVerificationRequest.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
		log.debug("<<< Exiting validateOtpRequest(otpVerificationRequest={})", otpVerificationRequest);
    }

}
