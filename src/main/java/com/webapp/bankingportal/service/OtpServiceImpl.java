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
        if (!isOtpRetryLimitExceeded(otpInfo)) {
            return;
        }

        val now = LocalDateTime.now();

        if (otpLimitReachedTime == null) {
            otpLimitReachedTime = now;
        }

        val waitingMinutes = OTP_RESET_WAITING_TIME_MINUTES - otpLimitReachedTime.until(now, ChronoUnit.MINUTES);

				log.debug("<<< Exiting validateOtpWithinRetryLimit(otpInfo=<redacted>)");
        throw new OtpRetryLimitExceededException(
                String.format(ApiMessages.OTP_GENERATION_LIMIT_EXCEEDED.getMessage(), waitingMinutes));
    }

    private boolean isOtpRetryLimitExceeded(OtpInfo otpInfo) {
        val attempts = getOtpAttempts(otpInfo.getAccountNumber());
        if (attempts < OTP_ATTEMPTS_LIMIT) {
            return false;
        }

        if (isOtpResetWaitingTimeExceeded()) {
            resetOtpAttempts(otpInfo.getAccountNumber());
            return false;
        }

        val now = LocalDateTime.now();

        return otpInfo.getGeneratedAt().isAfter(now.minusMinutes(OTP_RETRY_LIMIT_WINDOW_MINUTES));
    }

    private boolean isOtpResetWaitingTimeExceeded() {
		log.debug(">>> Entering isOtpResetWaitingTimeExceeded()");
        val now = LocalDateTime.now();
		log.debug("isOtpResetWaitingTimeExceeded(): now → {}", now);
		log.debug("<<< Exiting isOtpResetWaitingTimeExceeded()");
        return otpLimitReachedTime != null
                && otpLimitReachedTime.isBefore(now.minusMinutes(OTP_RESET_WAITING_TIME_MINUTES));
    }

    private void incrementOtpAttempts(String accountNumber) {
		log.debug(">>> Entering incrementOtpAttempts(accountNumber={})", accountNumber);
        if (!validationUtil.doesAccountExist(accountNumber)) {
			log.warn("Throwing AccountDoesNotExistException from incrementOtpAttempts(accountNumber={})", accountNumber);
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val cache = cacheManager.getCache("otpAttempts");
		log.debug("incrementOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
        if (cache != null) {
            cache.put(accountNumber, getOtpAttempts(accountNumber) + 1);
        }
		log.debug("<<< Exiting incrementOtpAttempts(accountNumber={})", accountNumber);
    }

    private void resetOtpAttempts(String accountNumber) {
		log.debug(">>> Entering resetOtpAttempts(accountNumber={})", accountNumber);
        otpLimitReachedTime = null;
        val cache = cacheManager.getCache("otpAttempts");
		log.debug("resetOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
		log.debug("resetOtpAttempts(accountNumber={}): otpLimitReachedTime → {}", accountNumber, otpLimitReachedTime);
        if (cache != null) {
            cache.put(accountNumber, 0);
        }
		log.debug("<<< Exiting resetOtpAttempts(accountNumber={})", accountNumber);
    }

    private int getOtpAttempts(String accountNumber) {
		log.debug(">>> Entering getOtpAttempts(accountNumber={})", accountNumber);
        var otpAttempts = 0;
        val cache = cacheManager.getCache("otpAttempts");
		log.debug("getOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
		log.debug("getOtpAttempts(accountNumber={}): otpAttempts → {}", accountNumber, otpAttempts);
        if (cache == null) {
            return otpAttempts;
        }

        val value = cache.get(accountNumber, Integer.class);
        if (value != null) {
            otpAttempts = value;
        }

		log.debug("<<< Exiting getOtpAttempts(accountNumber={})", accountNumber);
        return otpAttempts;
    }

    private String generateNewOTP(String accountNumber) {
		log.debug(">>> Entering generateNewOTP(accountNumber={})", accountNumber);
        val random = new Random();
        val otpValue = 100_000 + random.nextInt(900_000);
		log.debug("generateNewOTP(accountNumber={}): otpValue → {}", accountNumber, otpValue);
		long start = System.currentTimeMillis();
        val otp = String.valueOf(otpValue);
		log.debug("generateNewOTP(accountNumber={}): otp → {}", accountNumber, otp);

        otpInfoRepository.save(new OtpInfo(accountNumber, otp, LocalDateTime.now()));
		log.debug("generateNewOTP(accountNumber={}): random → {}", accountNumber, random);
		log.info("generateNewOTP(accountNumber)={}: save query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));

		log.debug("<<< Exiting generateNewOTP(accountNumber={})", accountNumber);
        return otp;
    }

    @Override
    public CompletableFuture<Void> sendOTPByEmail(String email, String name, String accountNumber, String otp) {
        val emailText = emailService.getOtpLoginEmailTemplate(name, "xxx" + accountNumber.substring(3), otp);
        return emailService.sendEmail(email, ApiMessages.EMAIL_SUBJECT_OTP.getMessage(), emailText);
    }

    @Override
    public boolean validateOTP(String accountNumber, String otp) {
		long start = System.currentTimeMillis();
        val otpInfo = otpInfoRepository.findByAccountNumberAndOtp(accountNumber, otp);
        if (otpInfo == null) {
            throw new InvalidOtpException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }

        return !isOtpExpired(otpInfo);
    }

    private boolean isOtpExpired(OtpInfo otpInfo) {
        val now = LocalDateTime.now();
        val generatedAt = otpInfo.getGeneratedAt();
        val expired = generatedAt.isBefore(now.minusMinutes(OTP_EXPIRY_MINUTES));
        if (expired) {
            otpInfoRepository.delete(otpInfo);
        }

        return expired;
    }

}
