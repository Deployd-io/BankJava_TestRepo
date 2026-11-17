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
		log.error(">>> Entering generatePasswordResetToken(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        long start = System.currentTimeMillis();
        val existingToken = passwordResetTokenRepository.findByUser(user);
        if (isExistingTokenValid(existingToken)) {
            return existingToken.getToken();
        }
		log.error("generatePasswordResetToken(user)={}: find query executed in {} ms", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", (System.currentTimeMillis() - start));
		log.error("generatePasswordResetToken(user={}): existingToken → <redacted>", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");

        val token = UUID.randomUUID().toString();
		log.error("generatePasswordResetToken(user={}): token → <redacted>", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        val expiryDateTime = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
		log.error("generatePasswordResetToken(user={}): expiryDateTime → {}", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", expiryDateTime);
        val resetToken = new PasswordResetToken(token, user, expiryDateTime);
		log.error("generatePasswordResetToken(user={}): resetToken → <redacted>", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        passwordResetTokenRepository.save(resetToken);
		log.error("generatePasswordResetToken(user)={}: save query executed in {} ms", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", (System.currentTimeMillis() - start));

		log.warn("<<< Exiting generatePasswordResetToken(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        return token;
    }

    @Override
    public boolean verifyPasswordResetToken(String token, User user) {
		log.error(">>> Entering verifyPasswordResetToken(token=<redacted>,user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
		log.warn("<<< Exiting verifyPasswordResetToken(token=<redacted>,user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
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
		log.error(">>> Entering sendOtpForPasswordReset(otpRequest={})", "[identifier=" + otpRequest.identifier() + "]");
        val user = userService.getUserByIdentifier(otpRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
		log.error("sendOtpForPasswordReset(otpRequest={}): accountNumber → {}", "[identifier=" + otpRequest.identifier() + "]", accountNumber);
        val generatedOtp = otpService.generateOTP(accountNumber);
		log.error("sendOtpForPasswordReset(otpRequest={}): generatedOtp → <redacted>", "[identifier=" + otpRequest.identifier() + "]");
		log.error("sendOtpForPasswordReset(otpRequest={}): user → <redacted>", "[identifier=" + otpRequest.identifier() + "]");

        ResponseEntity<String> result = sendOtpEmail(user, accountNumber, generatedOtp);
		log.warn("<<< Exiting sendOtpForPasswordReset(otpRequest={})", "[identifier=" + otpRequest.identifier() + "]");
        return result;
    }

    @Override
    public ResponseEntity<String> verifyOtpAndIssueResetToken(OtpVerificationRequest otpVerificationRequest) {
		log.error(">>> Entering verifyOtpAndIssueResetToken(otpVerificationRequest={})", "[identifier=" + otpVerificationRequest.identifier() + "]");
        validateOtpRequest(otpVerificationRequest);
        val user = userService.getUserByIdentifier(otpVerificationRequest.identifier());
		log.error("verifyOtpAndIssueResetToken(otpVerificationRequest={}): user → <redacted>", "[identifier=" + otpVerificationRequest.identifier() + "]");
        val accountNumber = user.getAccount().getAccountNumber();
		log.error("verifyOtpAndIssueResetToken(otpVerificationRequest={}): accountNumber → {}", "[identifier=" + otpVerificationRequest.identifier() + "]", accountNumber);

        if (!otpService.validateOTP(accountNumber, otpVerificationRequest.otp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }

        String resetToken = generatePasswordResetToken(user);
		log.error("verifyOtpAndIssueResetToken(otpVerificationRequest={}): resetToken → <redacted>", "[identifier=" + otpVerificationRequest.identifier() + "]");
        ResponseEntity<String> result = ResponseEntity.ok(String.format(ApiMessages.PASSWORD_RESET_TOKEN_ISSUED.getMessage(), resetToken));
		log.warn("<<< Exiting verifyOtpAndIssueResetToken(otpVerificationRequest={})", "[identifier=" + otpVerificationRequest.identifier() + "]");
        return result;
    }

    @Override
    @Transactional
    public ResponseEntity<String> resetPassword(ResetPasswordRequest resetPasswordRequest) {
		log.error(">>> Entering resetPassword(resetPasswordRequest={})", "[identifier=" + resetPasswordRequest.identifier() + "]");
        val user = userService.getUserByIdentifier(resetPasswordRequest.identifier());

		log.error("resetPassword(resetPasswordRequest={}): user → <redacted>", "[identifier=" + resetPasswordRequest.identifier() + "]");
        if (!verifyPasswordResetToken(resetPasswordRequest.resetToken(), user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiMessages.TOKEN_INVALID_ERROR.getMessage());
        }

        try {
            boolean passwordResetSuccessful = userService.resetPassword(user, resetPasswordRequest.newPassword());
			log.error("resetPassword(resetPasswordRequest={}): passwordResetSuccessful → <redacted>", "[identifier=" + resetPasswordRequest.identifier() + "]");
            if (passwordResetSuccessful) {
                return ResponseEntity.ok(ApiMessages.PASSWORD_RESET_SUCCESS.getMessage());
            } else {
                return ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
            }
        } catch (Exception e) {
			log.error("Exception in resetPassword(resetPasswordRequest={}): {}", "[identifier=" + resetPasswordRequest.identifier() + "]", e.getMessage(), e);
            ResponseEntity<String> result = ResponseEntity.internalServerError().body(ApiMessages.PASSWORD_RESET_FAILURE.getMessage());
			log.warn("<<< Exiting resetPassword(resetPasswordRequest={})", "[identifier=" + resetPasswordRequest.identifier() + "]");
            return result;
        }
    }

    private boolean isExistingTokenValid(PasswordResetToken existingToken) {
        return existingToken != null && existingToken.getExpiryDateTime().isAfter(LocalDateTime.now().plusMinutes(5));
    }

    private ResponseEntity<String> sendOtpEmail(User user, String accountNumber, String generatedOtp) {
		log.error(">>> Entering sendOtpEmail(user={},accountNumber={},generatedOtp=<redacted>)", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", accountNumber);
        val emailSendingFuture = otpService.sendOTPByEmail(user.getEmail(), user.getName(), accountNumber,
                generatedOtp);

        val successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        val failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

        ResponseEntity<String> logriteResult = emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
		log.warn("<<< Exiting sendOtpEmail(user={},accountNumber={},generatedOtp=<redacted>)", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", accountNumber);
        return logriteResult;
    }

    private void validateOtpRequest(OtpVerificationRequest otpVerificationRequest) {
		log.error(">>> Entering validateOtpRequest(otpVerificationRequest={})", "[identifier=" + otpVerificationRequest.identifier() + "]");
        if (otpVerificationRequest.identifier() == null || otpVerificationRequest.identifier().isEmpty()) {
			log.error("Throwing IllegalArgumentException from validateOtpRequest(otpVerificationRequest={})", "[identifier=" + otpVerificationRequest.identifier() + "]");
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (otpVerificationRequest.otp() == null || otpVerificationRequest.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
		log.warn("<<< Exiting validateOtpRequest(otpVerificationRequest={})", "[identifier=" + otpVerificationRequest.identifier() + "]");
    }

}