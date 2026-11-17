package com.webapp.bankingportal.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.OtpInfo;
import com.webapp.bankingportal.exception.AccountDoesNotExistException;
import com.webapp.bankingportal.exception.InvalidOtpException;
import com.webapp.bankingportal.exception.OtpRetryLimitExceededException;
import com.webapp.bankingportal.repository.OtpInfoRepository;
import com.webapp.bankingportal.util.ValidationUtil;
import com.webapp.bankingportal.util.ApiMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);

    public static final int OTP_ATTEMPTS_LIMIT = 3;
    public static final int OTP_EXPIRY_MINUTES = 5;
    public static final int OTP_RESET_WAITING_TIME_MINUTES = 10;
    public static final int OTP_RETRY_LIMIT_WINDOW_MINUTES = 15;

    private final CacheManager cacheManager;
    private final EmailService emailService;
    private final OtpInfoRepository otpInfoRepository;
    private final ValidationUtil validationUtil;

    private LocalDateTime otpLimitReachedTime = null;

    @Override
    public String generateOTP(String accountNumber) {
		log.error(">>> Entering generateOTP(accountNumber={})", accountNumber);
        if (!validationUtil.doesAccountExist(accountNumber)) {
			log.error("Throwing AccountDoesNotExistException from generateOTP(accountNumber={})", accountNumber);
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

		long start = System.currentTimeMillis();
        val existingOtpInfo = otpInfoRepository.findByAccountNumber(accountNumber);
		log.error("generateOTP(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
        if (existingOtpInfo == null) {
            incrementOtpAttempts(accountNumber);
		log.error("generateOTP(accountNumber={}): existingOtpInfo → <redacted>", accountNumber);
            return generateNewOTP(accountNumber);
        }

        validateOtpWithinRetryLimit(existingOtpInfo);

        if (isOtpExpired(existingOtpInfo)) {
            return generateNewOTP(accountNumber);
        }

        // Existing OTP is not expired
        existingOtpInfo.setGeneratedAt(LocalDateTime.now());
		log.debug("generateOTP(accountNumber={}): existingOtpInfo → <redacted>", accountNumber);
        incrementOtpAttempts(accountNumber);

        String result = existingOtpInfo.getOtp();
		log.warn("<<< Exiting generateOTP(accountNumber={})", accountNumber);
        return result;
    }

    private void validateOtpWithinRetryLimit(OtpInfo otpInfo) {
		log.error(">>> Entering validateOtpWithinRetryLimit(otpInfo={})", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"));
        if (!isOtpRetryLimitExceeded(otpInfo)) {
            return;
        }

        val now = LocalDateTime.now();
		log.error("validateOtpWithinRetryLimit(otpInfo={}): now → {}", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"), now);

        if (otpLimitReachedTime == null) {
            otpLimitReachedTime = now;
			log.debug("validateOtpWithinRetryLimit(otpInfo={}): otpLimitReachedTime → <redacted>", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"));
        }

        val waitingMinutes = OTP_RESET_WAITING_TIME_MINUTES - otpLimitReachedTime.until(now, ChronoUnit.MINUTES);
		log.error("validateOtpWithinRetryLimit(otpInfo={}): waitingMinutes → {}", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"), waitingMinutes);

		log.error("Throwing OtpRetryLimitExceededException from validateOtpWithinRetryLimit(otpInfo={})", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"));
				log.warn("<<< Exiting validateOtpWithinRetryLimit(otpInfo={})", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"));
        throw new OtpRetryLimitExceededException(
                String.format(ApiMessages.OTP_GENERATION_LIMIT_EXCEEDED.getMessage(), waitingMinutes));
    }

    private boolean isOtpRetryLimitExceeded(OtpInfo otpInfo) {
		log.error(">>> Entering isOtpRetryLimitExceeded(otpInfo={})", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"));
        val attempts = getOtpAttempts(otpInfo.getAccountNumber());
        if (attempts < OTP_ATTEMPTS_LIMIT) {
			log.error("isOtpRetryLimitExceeded(otpInfo={}): attempts → {}", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"), attempts);
            return false;
        }

        if (isOtpResetWaitingTimeExceeded()) {
            resetOtpAttempts(otpInfo.getAccountNumber());
            return false;
        }

        val now = LocalDateTime.now();
		log.error("isOtpRetryLimitExceeded(otpInfo={}): now → {}", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"), now);

        boolean result = otpInfo.getGeneratedAt().isAfter(now.minusMinutes(OTP_RETRY_LIMIT_WINDOW_MINUTES));
		log.warn("<<< Exiting isOtpRetryLimitExceeded(otpInfo={})", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"));
        return result;
    }

    private boolean isOtpResetWaitingTimeExceeded() {
		log.error(">>> Entering isOtpResetWaitingTimeExceeded()");
        val now = LocalDateTime.now();
		log.error("isOtpResetWaitingTimeExceeded(): now → {}", now);
        boolean result = otpLimitReachedTime != null
                && otpLimitReachedTime.isBefore(now.minusMinutes(OTP_RESET_WAITING_TIME_MINUTES));
		log.warn("<<< Exiting isOtpResetWaitingTimeExceeded()");
        return result;
    }

    private void incrementOtpAttempts(String accountNumber) {
		log.error(">>> Entering incrementOtpAttempts(accountNumber={})", accountNumber);
        if (!validationUtil.doesAccountExist(accountNumber)) {
			log.error("Throwing AccountDoesNotExistException from incrementOtpAttempts(accountNumber={})", accountNumber);
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val cache = cacheManager.getCache("otpAttempts");
		log.error("incrementOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
        if (cache != null) {
            cache.put(accountNumber, getOtpAttempts(accountNumber) + 1);
        }
		log.warn("<<< Exiting incrementOtpAttempts(accountNumber={})", accountNumber);
    }

    private void resetOtpAttempts(String accountNumber) {
		log.error(">>> Entering resetOtpAttempts(accountNumber={})", accountNumber);
        otpLimitReachedTime = null;
        val cache = cacheManager.getCache("otpAttempts");
		log.error("resetOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
		log.debug("resetOtpAttempts(accountNumber={}): otpLimitReachedTime → <redacted>", accountNumber);
        if (cache != null) {
            cache.put(accountNumber, 0);
        }
		log.warn("<<< Exiting resetOtpAttempts(accountNumber={})", accountNumber);
    }

    private int getOtpAttempts(String accountNumber) {
		log.error(">>> Entering getOtpAttempts(accountNumber={})", accountNumber);
        var otpAttempts = 0;
        val cache = cacheManager.getCache("otpAttempts");
		log.error("getOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
        if (cache == null) {
            return otpAttempts;
        }

        val value = cache.get(accountNumber, Integer.class);
		log.error("getOtpAttempts(accountNumber={}): value → <redacted>", accountNumber);
        if (value != null) {
            otpAttempts = value;
        }

		log.warn("<<< Exiting getOtpAttempts(accountNumber={})", accountNumber);
        return otpAttempts;
    }

    private String generateNewOTP(String accountNumber) {
		log.error(">>> Entering generateNewOTP(accountNumber={})", accountNumber);
        val random = new Random();
        val otpValue = 100_000 + random.nextInt(900_000);
		log.error("generateNewOTP(accountNumber={}): otpValue → <redacted>", accountNumber);
		long start = System.currentTimeMillis();
        val otp = String.valueOf(otpValue);
		log.error("generateNewOTP(accountNumber={}): otp → <redacted>", accountNumber);

        otpInfoRepository.save(new OtpInfo(accountNumber, otp, LocalDateTime.now()));
		log.error("generateNewOTP(accountNumber)={}: save query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
		log.error("generateNewOTP(accountNumber={}): random → {}", accountNumber, random);

		log.warn("<<< Exiting generateNewOTP(accountNumber={})", accountNumber);
        return otp;
    }

    @Override
    public CompletableFuture<Void> sendOTPByEmail(String email, String name, String accountNumber, String otp) {
		log.error(">>> Entering sendOTPByEmail(email={},name={},accountNumber={},otp=<redacted>)", email, name, accountNumber);
        val emailText = emailService.getOtpLoginEmailTemplate(name, "xxx" + accountNumber.substring(3), otp);
		log.error("sendOTPByEmail(email={},name={},accountNumber={},otp=<redacted>): emailText → {}", email, name, accountNumber, emailText);
        CompletableFuture<Void> result = emailService.sendEmail(email, ApiMessages.EMAIL_SUBJECT_OTP.getMessage(), emailText);
		log.warn("<<< Exiting sendOTPByEmail(email={},name={},accountNumber={},otp=<redacted>)", email, name, accountNumber);
        return result;
    }

    @Override
    public boolean validateOTP(String accountNumber, String otp) {
		log.error(">>> Entering validateOTP(accountNumber={},otp=<redacted>)", accountNumber);
        long start = System.currentTimeMillis();
        val otpInfo = otpInfoRepository.findByAccountNumberAndOtp(accountNumber, otp);
        if (otpInfo == null) {
			log.error("Throwing InvalidOtpException from validateOTP(accountNumber={},otp=<redacted>)", accountNumber);
            throw new InvalidOtpException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
		log.error("validateOTP(accountNumber,otp)={},<redacted>: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
		log.error("validateOTP(accountNumber={},otp=<redacted>): otpInfo → <redacted>", accountNumber);

        boolean result = !isOtpExpired(otpInfo);
		log.warn("<<< Exiting validateOTP(accountNumber={},otp=<redacted>)", accountNumber);
        return result;
    }

    private boolean isOtpExpired(OtpInfo otpInfo) {
		log.error(">>> Entering isOtpExpired(otpInfo={})", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"));
        val now = LocalDateTime.now();
        val generatedAt = otpInfo.getGeneratedAt();
		log.error("isOtpExpired(otpInfo={}): generatedAt → {}", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"), generatedAt);
		long start = System.currentTimeMillis();
        val expired = generatedAt.isBefore(now.minusMinutes(OTP_EXPIRY_MINUTES));
		log.error("isOtpExpired(otpInfo={}): expired → {}", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"), expired);
        if (expired) {
            otpInfoRepository.delete(otpInfo);
			log.error("isOtpExpired(otpInfo)={}: delete query executed in {} ms", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"), (System.currentTimeMillis() - start));
			log.error("isOtpExpired(otpInfo={}): now → {}", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"), now);
        }

		log.warn("<<< Exiting isOtpExpired(otpInfo={})", (otpInfo == null ? "null" : "[id=" + otpInfo.getId() + ", accountNumber=" + otpInfo.getAccountNumber() + ", generatedAt=" + otpInfo.getGeneratedAt() + "]"));
        return expired;
    }

}
