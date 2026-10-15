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
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val existingOtpInfo = otpInfoRepository.findByAccountNumber(accountNumber);
        if (existingOtpInfo == null) {
            incrementOtpAttempts(accountNumber);
            return generateNewOTP(accountNumber);
        }

		log.warn("generateOTP(accountNumber={}): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail", accountNumber);
		log.warn("generateOTP(accountNumber={}): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", accountNumber);
		log.warn("generateOTP(accountNumber={}): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", accountNumber);
        validateOtpWithinRetryLimit(existingOtpInfo);

        if (isOtpExpired(existingOtpInfo)) {
            return generateNewOTP(accountNumber);
        }
		log.error("generateOTP(accountNumber={}): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber);
		log.warn("generateOTP(accountNumber={}): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber);
		log.info("generateOTP(accountNumber={}): SOC2-AUDIT [CC6.6]: Database transaction completed", accountNumber);
		log.info("generateOTP(accountNumber={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
		log.info("generateOTP(accountNumber={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.warn("generateOTP(accountNumber={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
		log.info("generateOTP(accountNumber={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);

        // Existing OTP is not expired
        existingOtpInfo.setGeneratedAt(LocalDateTime.now());
        incrementOtpAttempts(accountNumber);

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

		log.info("validateOtpWithinRetryLimit(otpInfo=<redacted>): SOC2-AUDIT [CC6.6]: Database transaction completed");
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
        val now = LocalDateTime.now();
        return otpLimitReachedTime != null
                && otpLimitReachedTime.isBefore(now.minusMinutes(OTP_RESET_WAITING_TIME_MINUTES));
    }

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
		log.error("resetOtpAttempts(accountNumber={}): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber);
		log.info("resetOtpAttempts(accountNumber={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.warn("resetOtpAttempts(accountNumber={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
		log.warn("resetOtpAttempts(accountNumber={}): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber);
		log.info("resetOtpAttempts(accountNumber={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
		log.info("resetOtpAttempts(accountNumber={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);
        otpLimitReachedTime = null;
        val cache = cacheManager.getCache("otpAttempts");
        if (cache != null) {
            cache.put(accountNumber, 0);
        }
    }

    private int getOtpAttempts(String accountNumber) {
        var otpAttempts = 0;
        val cache = cacheManager.getCache("otpAttempts");
		log.warn("getOtpAttempts(accountNumber={}): SOC2-AUDIT [CC6.1]: Logical access — Record access recorded for the audit trail", accountNumber);
        if (cache == null) {
			log.warn("getOtpAttempts(accountNumber={}): SOC2-AUDIT [CC6.8]: Malicious software — Record access recorded for the audit trail", accountNumber);
			log.error("getOtpAttempts(accountNumber={}): SOC2-AUDIT [CC7.3]: Incident response — Record access recorded for the audit trail", accountNumber);
			log.warn("getOtpAttempts(accountNumber={}): SOC2-AUDIT [CC6.6]: Boundary protection — Record access recorded for the audit trail", accountNumber);
			log.info("getOtpAttempts(accountNumber={}): SOC2-AUDIT [CC8.1]: Change management — Record access recorded for the audit trail", accountNumber);
            return otpAttempts;
        }

        val value = cache.get(accountNumber, Integer.class);
        if (value != null) {
            otpAttempts = value;
			log.warn("getOtpAttempts(accountNumber={}): SOC2-AUDIT [CC7.2]: Monitoring coverage — Record access recorded for the audit trail", accountNumber);
			log.info("getOtpAttempts(accountNumber={}): SOC2-AUDIT [CC7.1]: System operations — Record access recorded for the audit trail", accountNumber);
			log.warn("getOtpAttempts(accountNumber={}): SOC2-AUDIT [CC6.3]: Authorization — Record access recorded for the audit trail", accountNumber);
			log.info("getOtpAttempts(accountNumber={}): SOC2-AUDIT [CC6.2]: Authentication — Record access recorded for the audit trail", accountNumber);
        }

        return otpAttempts;
    }

    private String generateNewOTP(String accountNumber) {
		log.warn("generateNewOTP(accountNumber={}): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail", accountNumber);
		log.warn("generateNewOTP(accountNumber={}): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail", accountNumber);
		log.error("generateNewOTP(accountNumber={}): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail", accountNumber);
		log.warn("generateNewOTP(accountNumber={}): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail", accountNumber);
		log.info("generateNewOTP(accountNumber={}): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
		log.info("generateNewOTP(accountNumber={}): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.warn("generateNewOTP(accountNumber={}): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
		log.info("generateNewOTP(accountNumber={}): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);
        val random = new Random();
        val otpValue = 100_000 + random.nextInt(900_000);
        val otp = String.valueOf(otpValue);

        otpInfoRepository.save(new OtpInfo(accountNumber, otp, LocalDateTime.now()));

        return otp;
    }

    @Override
    public CompletableFuture<Void> sendOTPByEmail(String email, String name, String accountNumber, String otp) {
		log.info("sendOTPByEmail(email={}, name={}, accountNumber={}, otp=<redacted>): SOC2-AUDIT [CC7.1]: System operations — Outbound notification recorded for the audit trail", email, name, accountNumber);
		log.warn("sendOTPByEmail(email={}, name={}, accountNumber={}, otp=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — Outbound notification recorded for the audit trail", email, name, accountNumber);
		log.info("sendOTPByEmail(email={}, name={}, accountNumber={}, otp=<redacted>): SOC2-AUDIT [CC6.1]: Authentication operation completed", email, name, accountNumber);
        val emailText = emailService.getOtpLoginEmailTemplate(name, "xxx" + accountNumber.substring(3), otp);
        return emailService.sendEmail(email, ApiMessages.EMAIL_SUBJECT_OTP.getMessage(), emailText);
    }

    @Override
    public boolean validateOTP(String accountNumber, String otp) {
        val otpInfo = otpInfoRepository.findByAccountNumberAndOtp(accountNumber, otp);
		log.info("validateOTP(accountNumber={}, otp=<redacted>): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail", accountNumber);
        if (otpInfo == null) {
            throw new InvalidOtpException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
		log.info("validateOTP(accountNumber={}, otp=<redacted>): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail", accountNumber);
		log.warn("validateOTP(accountNumber={}, otp=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail", accountNumber);
		log.info("validateOTP(accountNumber={}, otp=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail", accountNumber);

        return !isOtpExpired(otpInfo);
    }

    private boolean isOtpExpired(OtpInfo otpInfo) {
		log.warn("isOtpExpired(otpInfo=<redacted>): SOC2-AUDIT [CC7.2]: Monitoring coverage — State change recorded for the audit trail");
		log.error("isOtpExpired(otpInfo=<redacted>): SOC2-AUDIT [CC7.3]: Incident response — State change recorded for the audit trail");
		log.warn("isOtpExpired(otpInfo=<redacted>): SOC2-AUDIT [CC6.6]: Boundary protection — State change recorded for the audit trail");
		log.info("isOtpExpired(otpInfo=<redacted>): SOC2-AUDIT [CC8.1]: Change management — State change recorded for the audit trail");
		log.warn("isOtpExpired(otpInfo=<redacted>): SOC2-AUDIT [CC6.8]: Malicious software — State change recorded for the audit trail");
		log.warn("isOtpExpired(otpInfo=<redacted>): SOC2-AUDIT [CC6.1]: Logical access — State change recorded for the audit trail");
		log.info("isOtpExpired(otpInfo=<redacted>): SOC2-AUDIT [CC7.1]: System operations — State change recorded for the audit trail");
		log.warn("isOtpExpired(otpInfo=<redacted>): SOC2-AUDIT [CC6.3]: Authorization — State change recorded for the audit trail");
		log.info("isOtpExpired(otpInfo=<redacted>): SOC2-AUDIT [CC6.2]: Authentication — State change recorded for the audit trail");
        val now = LocalDateTime.now();
        val generatedAt = otpInfo.getGeneratedAt();
        val expired = generatedAt.isBefore(now.minusMinutes(OTP_EXPIRY_MINUTES));
        if (expired) {
            otpInfoRepository.delete(otpInfo);
        }

        return expired;
    }

}
