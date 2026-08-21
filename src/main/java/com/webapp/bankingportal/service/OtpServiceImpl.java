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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

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
		log.debug(">>> Entering generateOTP(accountNumber={})", accountNumber);
        if (!validationUtil.doesAccountExist(accountNumber)) {
			log.warn("Throwing AccountDoesNotExistException from generateOTP(accountNumber={})", accountNumber);
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

		long start = System.currentTimeMillis();
        val existingOtpInfo = otpInfoRepository.findByAccountNumber(accountNumber);
        if (existingOtpInfo == null) {
            incrementOtpAttempts(accountNumber);
            return generateNewOTP(accountNumber);
        }
		log.info("generateOTP(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));

        validateOtpWithinRetryLimit(existingOtpInfo);

        if (isOtpExpired(existingOtpInfo)) {
            return generateNewOTP(accountNumber);
        }

        // Existing OTP is not expired
        existingOtpInfo.setGeneratedAt(LocalDateTime.now());
		log.debug("generateOTP(accountNumber={}): existingOtpInfo → {}", accountNumber, existingOtpInfo);
        incrementOtpAttempts(accountNumber);

		log.debug("<<< Exiting generateOTP(accountNumber={})", accountNumber);
        return existingOtpInfo.getOtp();
    }

    private void validateOtpWithinRetryLimit(OtpInfo otpInfo) {
		log.debug(">>> Entering validateOtpWithinRetryLimit(otpInfo=<redacted>)");
        if (!isOtpRetryLimitExceeded(otpInfo)) {
            return;
        }

        val now = LocalDateTime.now();
		log.debug("validateOtpWithinRetryLimit(otpInfo=<redacted>): now → {}", now);

        if (otpLimitReachedTime == null) {
            otpLimitReachedTime = now;
			log.debug("validateOtpWithinRetryLimit(otpInfo=<redacted>): otpLimitReachedTime → {}", otpLimitReachedTime);
        }

        val waitingMinutes = OTP_RESET_WAITING_TIME_MINUTES - otpLimitReachedTime.until(now, ChronoUnit.MINUTES);
		log.debug("validateOtpWithinRetryLimit(otpInfo=<redacted>): waitingMinutes → {}", waitingMinutes);

				log.warn("Throwing OtpRetryLimitExceededException from validateOtpWithinRetryLimit(otpInfo=<redacted>)");
				log.debug("<<< Exiting validateOtpWithinRetryLimit(otpInfo=<redacted>)");
        throw new OtpRetryLimitExceededException(
                String.format(ApiMessages.OTP_GENERATION_LIMIT_EXCEEDED.getMessage(), waitingMinutes));
    }

    private boolean isOtpRetryLimitExceeded(OtpInfo otpInfo) {
		log.debug(">>> Entering isOtpRetryLimitExceeded(otpInfo=<redacted>)");
        val attempts = getOtpAttempts(otpInfo.getAccountNumber());
        if (attempts < OTP_ATTEMPTS_LIMIT) {
			log.debug("isOtpRetryLimitExceeded(otpInfo=<redacted>): attempts → {}", attempts);
            return false;
        }

        if (isOtpResetWaitingTimeExceeded()) {
            resetOtpAttempts(otpInfo.getAccountNumber());
            return false;
        }

        val now = LocalDateTime.now();
		log.debug("isOtpRetryLimitExceeded(otpInfo=<redacted>): now → {}", now);

		log.debug("<<< Exiting isOtpRetryLimitExceeded(otpInfo=<redacted>)");
        return otpInfo.getGeneratedAt().isAfter(now.minusMinutes(OTP_RETRY_LIMIT_WINDOW_MINUTES));
    }

    private boolean isOtpResetWaitingTimeExceeded() {
        val now = LocalDateTime.now();
        return otpLimitReachedTime != null
                && otpLimitReachedTime.isBefore(now.minusMinutes(OTP_RESET_WAITING_TIME_MINUTES));
    }

    private void incrementOtpAttempts(String accountNumber) {
        if (!validationUtil.doesAccountExist(accountNumber)) {
			log.warn("Throwing AccountDoesNotExistException from incrementOtpAttempts(accountNumber={})", accountNumber);
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val cache = cacheManager.getCache("otpAttempts");
        if (cache != null) {
            cache.put(accountNumber, getOtpAttempts(accountNumber) + 1);
        }
    }

    private void resetOtpAttempts(String accountNumber) {
        otpLimitReachedTime = null;
        val cache = cacheManager.getCache("otpAttempts");
        if (cache != null) {
            cache.put(accountNumber, 0);
        }
    }

    private int getOtpAttempts(String accountNumber) {
        var otpAttempts = 0;
        val cache = cacheManager.getCache("otpAttempts");
        if (cache == null) {
            return otpAttempts;
        }

        val value = cache.get(accountNumber, Integer.class);
        if (value != null) {
            otpAttempts = value;
        }

        return otpAttempts;
    }

    private String generateNewOTP(String accountNumber) {
        val random = new Random();
        val otpValue = 100_000 + random.nextInt(900_000);
		long start = System.currentTimeMillis();
        val otp = String.valueOf(otpValue);

        otpInfoRepository.save(new OtpInfo(accountNumber, otp, LocalDateTime.now()));
		log.info("generateNewOTP(accountNumber)={}: save query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));

        return otp;
    }

    @Override
    public CompletableFuture<Void> sendOTPByEmail(String email, String name, String accountNumber, String otp) {
        val emailText = emailService.getOtpLoginEmailTemplate(name, "xxx" + accountNumber.substring(3), otp);
        return emailService.sendEmail(email, ApiMessages.EMAIL_SUBJECT_OTP.getMessage(), emailText);
    }

    @Override
    public boolean validateOTP(String accountNumber, String otp) {
        val otpInfo = otpInfoRepository.findByAccountNumberAndOtp(accountNumber, otp);
        if (otpInfo == null) {
			log.warn("Throwing InvalidOtpException from validateOTP(accountNumber={},otp=<redacted>)", accountNumber);
            throw new InvalidOtpException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
		log.info("validateOTP(accountNumber,otp)={},{}: find query executed in {} ms", accountNumber, otp, (System.currentTimeMillis() - start));

        return !isOtpExpired(otpInfo);
    }

    private boolean isOtpExpired(OtpInfo otpInfo) {
        val now = LocalDateTime.now();
        val generatedAt = otpInfo.getGeneratedAt();
		long start = System.currentTimeMillis();
        val expired = generatedAt.isBefore(now.minusMinutes(OTP_EXPIRY_MINUTES));
        if (expired) {
			log.info("isOtpExpired(otpInfo)={}: delete query executed in {} ms", otpInfo, (System.currentTimeMillis() - start));
            otpInfoRepository.delete(otpInfo);
        }

        return expired;
    }

}
