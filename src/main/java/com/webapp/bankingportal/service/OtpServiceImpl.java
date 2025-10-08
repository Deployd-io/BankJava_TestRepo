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
        if (!validationUtil.doesAccountExist(accountNumber)) {
			log.debug(">>> Entering generateOTP(accountNumber={})", accountNumber);
			long start = System.currentTimeMillis();
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

		log.debug("<<< Exiting generateOTP(accountNumber={})", accountNumber);
        val existingOtpInfo = otpInfoRepository.findByAccountNumber(accountNumber);
		log.debug("generateOTP(accountNumber={}): existingOtpInfo → {}", accountNumber, existingOtpInfo);
		log.info("generateOTP(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
        if (existingOtpInfo == null) {
            incrementOtpAttempts(accountNumber);
			log.debug(">>> Entering incrementOtpAttempts(accountNumber={})", accountNumber);
			log.debug("<<< Exiting incrementOtpAttempts(accountNumber={})", accountNumber);
			log.debug("incrementOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
            return generateNewOTP(accountNumber);
        }

        validateOtpWithinRetryLimit(existingOtpInfo);

        if (isOtpExpired(existingOtpInfo)) {
			log.debug("<<< Exiting generateNewOTP(isOtpExpired(existingOtpInfo={})", isOtpExpired(existingOtpInfo);
			log.debug(">>> Entering isOtpExpired(otpInfo={})", otpInfo);
			log.debug("isOtpExpired(otpInfo={}): now → {}", otpInfo, now);
			log.debug("isOtpExpired(otpInfo={}): generatedAt → {}", otpInfo, generatedAt);
			log.debug("<<< Exiting isOtpExpired(otpInfo={})", otpInfo);
			log.debug("isOtpExpired(otpInfo={}): expired → {}", otpInfo, expired);
            return generateNewOTP(accountNumber);
        }

        // Existing OTP is not expired
        existingOtpInfo.setGeneratedAt(LocalDateTime.now());
        incrementOtpAttempts(accountNumber);

        return existingOtpInfo.getOtp();
    }

    private void validateOtpWithinRetryLimit(OtpInfo otpInfo) {
        if (!isOtpRetryLimitExceeded(otpInfo)) {
			log.debug(">>> Entering validateOtpWithinRetryLimit(otpInfo={})", otpInfo);
			log.debug(">>> Entering isOtpRetryLimitExceeded(otpInfo={})", otpInfo);
			log.debug("isOtpRetryLimitExceeded(otpInfo={}): attempts → {}", otpInfo, attempts);
			log.debug("isOtpRetryLimitExceeded(otpInfo={}): now → {}", otpInfo, now);
			log.debug("<<< Exiting isOtpRetryLimitExceeded(otpInfo={})", otpInfo);
            return;
        }

        val now = LocalDateTime.now();
		log.debug("validateOtpWithinRetryLimit(otpInfo={}): now → {}", otpInfo, now);
		log.debug("<<< Exiting validateOtpWithinRetryLimit(otpInfo={})", otpInfo);

        if (otpLimitReachedTime == null) {
            otpLimitReachedTime = now;
        }

        val waitingMinutes = OTP_RESET_WAITING_TIME_MINUTES - otpLimitReachedTime.until(now, ChronoUnit.MINUTES);

        throw new OtpRetryLimitExceededException(
                String.format(ApiMessages.OTP_GENERATION_LIMIT_EXCEEDED.getMessage(), waitingMinutes));
    }

    private boolean isOtpRetryLimitExceeded(OtpInfo otpInfo) {
        val attempts = getOtpAttempts(otpInfo.getAccountNumber());
        if (attempts < OTP_ATTEMPTS_LIMIT) {
			log.debug(">>> Entering getOtpAttempts(accountNumber={})", accountNumber);
			log.debug("getOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
			log.debug("getOtpAttempts(accountNumber={}): value → {}", accountNumber, value);
			log.debug("getOtpAttempts(accountNumber={}): otpAttempts → {}", accountNumber, otpAttempts);
			log.debug("<<< Exiting getOtpAttempts(accountNumber={})", accountNumber);
            return false;
        }

        if (isOtpResetWaitingTimeExceeded()) {
            resetOtpAttempts(otpInfo.getAccountNumber());
			log.debug(">>> Entering resetOtpAttempts(accountNumber={})", accountNumber);
			log.debug("resetOtpAttempts(accountNumber={}): otpLimitReachedTime → {}", accountNumber, otpLimitReachedTime);
			log.debug("resetOtpAttempts(accountNumber={}): cache → {}", accountNumber, cache);
			log.debug("<<< Exiting resetOtpAttempts(accountNumber={})", accountNumber);
            return false;
        }

        val now = LocalDateTime.now();

        return otpInfo.getGeneratedAt().isAfter(now.minusMinutes(OTP_RETRY_LIMIT_WINDOW_MINUTES));
    }

    private boolean isOtpResetWaitingTimeExceeded() {
        val now = LocalDateTime.now();
		log.debug(">>> Entering isOtpResetWaitingTimeExceeded()");
		log.debug("isOtpResetWaitingTimeExceeded(): now → {}", now);
        return otpLimitReachedTime != null
                && otpLimitReachedTime.isBefore(now.minusMinutes(OTP_RESET_WAITING_TIME_MINUTES));
    }
	log.debug("<<< Exiting isOtpResetWaitingTimeExceeded()");

    private void incrementOtpAttempts(String accountNumber) {
        if (!validationUtil.doesAccountExist(accountNumber)) {
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
		log.debug(">>> Entering Random(accountNumber={})", accountNumber);
		long start = System.currentTimeMillis();
        val otpValue = 100_000 + random.nextInt(900_000);
        val otp = String.valueOf(otpValue);

        otpInfoRepository.save(new OtpInfo(accountNumber, otp, LocalDateTime.now()));
		log.info("Random(accountNumber)={}: save query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));

        return otp;
    }

	log.debug("<<< Exiting Random(accountNumber={})", accountNumber);
    @Override
    public CompletableFuture<Void> sendOTPByEmail(String email, String name, String accountNumber, String otp) {
		log.info("sendOTPByEmail(email,name,accountNumber,otp)={},{},{},{}: query query executed in {} ms", email, name, accountNumber, otp, (System.currentTimeMillis() - start));
        val emailText = emailService.getOtpLoginEmailTemplate(name, "xxx" + accountNumber.substring(3), otp);
		log.debug(">>> Entering sendOTPByEmail(email={},name={},accountNumber={},otp={})", email, name, accountNumber, otp);
		log.debug("sendOTPByEmail(email={},name={},accountNumber={},otp={}): emailText → {}", email, name, accountNumber, otp, emailText);
		long start = System.currentTimeMillis();
		log.debug("<<< Exiting sendOTPByEmail(email={},name={},accountNumber={},otp={})", email, name, accountNumber, otp);
        return emailService.sendEmail(email, ApiMessages.EMAIL_SUBJECT_OTP.getMessage(), emailText);
    }

    @Override
    public boolean validateOTP(String accountNumber, String otp) {
        val otpInfo = otpInfoRepository.findByAccountNumberAndOtp(accountNumber, otp);
		log.debug(">>> Entering validateOTP(accountNumber={},otp={})", accountNumber, otp);
		log.debug("validateOTP(accountNumber={},otp={}): otpInfo → {}", accountNumber, otp, otpInfo);
		long start = System.currentTimeMillis();
		log.info("validateOTP(accountNumber,otp)={},{}: find query executed in {} ms", accountNumber, otp, (System.currentTimeMillis() - start));
        if (otpInfo == null) {
            throw new InvalidOtpException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }

		log.debug("<<< Exiting validateOTP(accountNumber={},otp={})", accountNumber, otp);
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
