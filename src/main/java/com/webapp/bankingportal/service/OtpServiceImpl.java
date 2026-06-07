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
public static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);

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
		long start = System.currentTimeMillis();
        if (!validationUtil.doesAccountExist(accountNumber)) {
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val existingOtpInfo = otpInfoRepository.findByAccountNumber(accountNumber);
        if (existingOtpInfo == null) {
			log.debug("generateOTP(accountNumber={}): existingOtpInfo → {}", accountNumber, existingOtpInfo);
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
        incrementOtpAttempts(accountNumber);

		log.info("generateOTP(accountNumber)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", accountNumber);
		log.debug("<<< Exiting generateOTP(accountNumber={})", accountNumber);
        return existingOtpInfo.getOtp();
    }

    private void validateOtpWithinRetryLimit(OtpInfo otpInfo) {
		log.debug(">>> Entering validateOtpWithinRetryLimit(otpInfo={})", otpInfo);
        if (!isOtpRetryLimitExceeded(otpInfo)) {
            return;
        }

        val now = LocalDateTime.now();

		log.debug("validateOtpWithinRetryLimit(otpInfo={}): now → {}", otpInfo, now);
        if (otpLimitReachedTime == null) {
            otpLimitReachedTime = now;
			log.debug("validateOtpWithinRetryLimit(otpInfo={}): otpLimitReachedTime → {}", otpInfo, otpLimitReachedTime);
        }

        val waitingMinutes = OTP_RESET_WAITING_TIME_MINUTES - otpLimitReachedTime.until(now, ChronoUnit.MINUTES);
		log.info("validateOtpWithinRetryLimit(otpInfo)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", otpInfo);

				log.debug("validateOtpWithinRetryLimit(otpInfo={}): waitingMinutes → {}", otpInfo, waitingMinutes);
				log.debug("<<< Exiting validateOtpWithinRetryLimit(otpInfo={})", otpInfo);
        throw new OtpRetryLimitExceededException(
                String.format(ApiMessages.OTP_GENERATION_LIMIT_EXCEEDED.getMessage(), waitingMinutes));
    }

    private boolean isOtpRetryLimitExceeded(OtpInfo otpInfo) {
		log.debug(">>> Entering isOtpRetryLimitExceeded(otpInfo={})", otpInfo);
        val attempts = getOtpAttempts(otpInfo.getAccountNumber());
        if (attempts < OTP_ATTEMPTS_LIMIT) {
			log.debug("isOtpRetryLimitExceeded(otpInfo={}): attempts → {}", otpInfo, attempts);
            return false;
        }

        if (isOtpResetWaitingTimeExceeded()) {
            resetOtpAttempts(otpInfo.getAccountNumber());
            return false;
        }

        val now = LocalDateTime.now();

		log.debug("isOtpRetryLimitExceeded(otpInfo={}): now → {}", otpInfo, now);
		log.debug("<<< Exiting isOtpRetryLimitExceeded(otpInfo={})", otpInfo);
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
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val cache = cacheManager.getCache("otpAttempts");
        if (cache != null) {
			log.debug("incrementOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
            cache.put(accountNumber, getOtpAttempts(accountNumber) + 1);
        }
		log.debug("<<< Exiting incrementOtpAttempts(accountNumber={})", accountNumber);
    }

    private void resetOtpAttempts(String accountNumber) {
		log.debug(">>> Entering resetOtpAttempts(accountNumber={})", accountNumber);
        otpLimitReachedTime = null;
        val cache = cacheManager.getCache("otpAttempts");
		log.debug("resetOtpAttempts(accountNumber={}): otpLimitReachedTime → {}", accountNumber, otpLimitReachedTime);
		log.debug("resetOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
        if (cache != null) {
            cache.put(accountNumber, 0);
        }
		log.debug("<<< Exiting resetOtpAttempts(accountNumber={})", accountNumber);
    }

    private int getOtpAttempts(String accountNumber) {
		log.debug(">>> Entering getOtpAttempts(accountNumber={})", accountNumber);
        var otpAttempts = 0;
        val cache = cacheManager.getCache("otpAttempts");
		log.debug("getOtpAttempts(accountNumber={}): otpAttempts → {}", accountNumber, otpAttempts);
        if (cache == null) {
			log.debug("getOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
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
		log.debug("generateNewOTP(accountNumber={}): random → {}", accountNumber, random);
		log.debug("generateNewOTP(accountNumber={}): otpValue → {}", accountNumber, otpValue);
        val otp = String.valueOf(otpValue);

		log.debug("generateNewOTP(accountNumber={}): otp → {}", accountNumber, otp);
        otpInfoRepository.save(new OtpInfo(accountNumber, otp, LocalDateTime.now()));

		log.debug("<<< Exiting generateNewOTP(accountNumber={})", accountNumber);
        return otp;
    }

    @Override
    public CompletableFuture<Void> sendOTPByEmail(String email, String name, String accountNumber, String otp) {
		log.debug(">>> Entering sendOTPByEmail(email={},name={},accountNumber={},otp={})", email, name, accountNumber, otp);
        val emailText = emailService.getOtpLoginEmailTemplate(name, "xxx" + accountNumber.substring(3), otp);
		log.debug("sendOTPByEmail(email={},name={},accountNumber={},otp={}): emailText → {}", email, name, accountNumber, otp, emailText);
		log.debug("<<< Exiting sendOTPByEmail(email={},name={},accountNumber={},otp={})", email, name, accountNumber, otp);
        return emailService.sendEmail(email, ApiMessages.EMAIL_SUBJECT_OTP.getMessage(), emailText);
    }

    @Override
    public boolean validateOTP(String accountNumber, String otp) {
		log.debug(">>> Entering validateOTP(accountNumber={},otp={})", accountNumber, otp);
        val otpInfo = otpInfoRepository.findByAccountNumberAndOtp(accountNumber, otp);
        if (otpInfo == null) {
			log.debug("validateOTP(accountNumber={},otp={}): otpInfo → {}", accountNumber, otp, otpInfo);
            throw new InvalidOtpException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }

		log.info("validateOTP(accountNumber,otp)={},{}): SOC2-AUDIT [CC6.6]: Database transaction completed", accountNumber, otp);
		log.debug("<<< Exiting validateOTP(accountNumber={},otp={})", accountNumber, otp);
        return !isOtpExpired(otpInfo);
    }

    private boolean isOtpExpired(OtpInfo otpInfo) {
		log.debug(">>> Entering isOtpExpired(otpInfo={})", otpInfo);
        val now = LocalDateTime.now();
        val generatedAt = otpInfo.getGeneratedAt();
		log.debug("isOtpExpired(otpInfo={}): now → {}", otpInfo, now);
		log.debug("isOtpExpired(otpInfo={}): generatedAt → {}", otpInfo, generatedAt);
        val expired = generatedAt.isBefore(now.minusMinutes(OTP_EXPIRY_MINUTES));
        if (expired) {
			log.debug("isOtpExpired(otpInfo={}): expired → {}", otpInfo, expired);
            otpInfoRepository.delete(otpInfo);
        }

		log.debug("<<< Exiting isOtpExpired(otpInfo={})", otpInfo);
        return expired;
    }

}
