package com.webapp.bankingportal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.repository.UserRepository;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class ValidationUtil {

    public static final Logger log = LoggerFactory.getLogger(ValidationUtil.class);
    public static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private final UserRepository userRepository;

    public static boolean isValidEmail(String identifier) {
		log.debug(">>> Entering isValidEmail(identifier={})", identifier);
        try {
            new InternetAddress(identifier).validate();
            return true;
        } catch (AddressException e) {
            log.warn("Invalid email address: {}", identifier);
        }

		log.debug("<<< Exiting isValidEmail(identifier={})", identifier);
        return false;
    }

    public static boolean isValidAccountNumber(String identifier) {
		log.debug(">>> Entering isValidAccountNumber(identifier={})", identifier);
        // Account number validation logic (e.g., length check)
        boolean result = identifier != null && identifier.length() == 6;
		log.debug("<<< Exiting isValidAccountNumber(identifier={}) -> {}", identifier, result);
        return result;
    }

    public static boolean isValidCountryCode(String countryCode) {
		log.debug(">>> Entering isValidCountryCode(countryCode={})", countryCode);
        if (!phoneNumberUtil.getSupportedRegions().contains(countryCode)) {
            return false;
        }

		log.debug("<<< Exiting isValidCountryCode(countryCode={})", countryCode);
        return true;
    }

    public static boolean isValidPhoneNumber(String phoneNumber, String countryCode) {
		log.debug(">>> Entering isValidPhoneNumber(phoneNumber={},countryCode={})", phoneNumber, countryCode);
        PhoneNumber parsedNumber = null;

		log.debug("isValidPhoneNumber(phoneNumber={},countryCode={}): parsedNumber → {}", phoneNumber, countryCode, parsedNumber);
        try {
            parsedNumber = phoneNumberUtil.parse(phoneNumber, countryCode);
        } catch (NumberParseException e) {
			log.error("Exception in isValidPhoneNumber(phoneNumber={},countryCode={}): {}", phoneNumber, countryCode, e.getMessage(), e);
		log.debug("<<< Exiting isValidPhoneNumber(phoneNumber={},countryCode={})", phoneNumber, countryCode);
            throw new UserInvalidException(String.format(ApiMessages.USER_PHONE_NUMBER_INVALID_ERROR.getMessage(), phoneNumber, countryCode));
        }

        return phoneNumberUtil.isValidNumber(parsedNumber);
    }

    public static void validatePassword(String password) {
		log.debug(">>> Entering validatePassword(password=<redacted>)");
        if (password.length() < 8) {
			log.error("Throwing UserInvalidException from validatePassword(password=<redacted>)");
            throw new UserInvalidException(ApiMessages.PASSWORD_TOO_SHORT_ERROR.getMessage());
        }

        if (password.length() >= 128) {
            throw new UserInvalidException(ApiMessages.PASSWORD_TOO_LONG_ERROR.getMessage());
        }

        if (password.matches(".*\\s.*")) {
            throw new UserInvalidException(ApiMessages.PASSWORD_CONTAINS_WHITESPACE_ERROR.getMessage());
        }

        val message = new StringBuilder();
		log.debug("validatePassword(password=<redacted>): message → {}", message);
        message.append("Password must contain at least ");

        var needsComma = false;
		log.debug("validatePassword(password=<redacted>): needsComma → {}", needsComma);
        if (!password.matches(".*[A-Z].*")) {
            message.append("one uppercase letter");
            needsComma = true;
        }

        if (!password.matches(".*[a-z].*")) {
            if (needsComma) {
                message.append(", ");
            }
            message.append("one lowercase letter");
            needsComma = true;
        }

        if (!password.matches(".*[0-9].*")) {
            if (needsComma) {
                message.append(", ");
            }
            message.append("one digit");
            needsComma = true;
        }

        if (!password.matches(".*[^A-Za-z0-9].*")) {
            if (needsComma) {
                message.append(", ");
            }
            message.append("one special character");
        }

        if (message.length() > "Password must contain at least ".length()) {
            val lastCommaIndex = message.lastIndexOf(",");
			log.debug("validatePassword(password=<redacted>): lastCommaIndex → {}", lastCommaIndex);
            if (lastCommaIndex > -1) {
                message.replace(lastCommaIndex, lastCommaIndex + 1, " and");
            }
            throw new UserInvalidException(message.toString());
        }
		log.debug("<<< Exiting validatePassword(password=<redacted>)");
    }

    public static void validateUserDetailsNotEmpty(User user) {
		log.debug(">>> Entering validateUserDetailsNotEmpty(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        if (user == null) {
			log.error("Throwing UserInvalidException from validateUserDetailsNotEmpty(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
            throw new UserInvalidException(ApiMessages.USER_DETAILS_EMPTY_ERROR.getMessage());
        }

        if (user.getName() == null || user.getName().isEmpty()) {
			log.error("Throwing UserInvalidException from validateUserDetailsNotEmpty(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
            throw new UserInvalidException(ApiMessages.USER_NAME_EMPTY_ERROR.getMessage());
        }

        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            throw new UserInvalidException(ApiMessages.USER_ADDRESS_EMPTY_ERROR.getMessage());
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserInvalidException(ApiMessages.USER_EMAIL_EMPTY_ERROR.getMessage());
        }

        if (user.getCountryCode() == null || user.getCountryCode().isEmpty()) {
            throw new UserInvalidException(ApiMessages.USER_COUNTRY_CODE_EMPTY_ERROR.getMessage());
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new UserInvalidException(ApiMessages.USER_PHONE_NUMBER_EMPTY_ERROR.getMessage());
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new UserInvalidException(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage());
        }
		log.debug("<<< Exiting validateUserDetailsNotEmpty(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
    }

    public static void validateUserDetails(User user) {
		log.debug(">>> Entering validateUserDetails(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        validateUserDetailsNotEmpty(user);

        if (!isValidEmail(user.getEmail())) {
			log.error("Throwing UserInvalidException from validateUserDetails(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
            throw new UserInvalidException(String.format(ApiMessages.USER_EMAIL_ADDRESS_INVALID_ERROR.getMessage(), user.getEmail()));
        }

        if (!isValidCountryCode(user.getCountryCode())) {
			log.error("Throwing UserInvalidException from validateUserDetails(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
            throw new UserInvalidException(String.format(ApiMessages.USER_COUNTRY_CODE_INVALID_ERROR.getMessage(), user.getCountryCode()));
        }

        if (!isValidPhoneNumber(user.getPhoneNumber(), user.getCountryCode())) {
            throw new UserInvalidException(String.format(ApiMessages.USER_PHONE_NUMBER_INVALID_ERROR.getMessage(), user.getPhoneNumber(), user.getCountryCode()));
        }

        validatePassword(user.getPassword());
		log.debug("<<< Exiting validateUserDetails(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
    }

    public void validateNewUser(User user) {
		log.debug(">>> Entering validateNewUser(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        validateUserDetails(user);
        if (doesEmailExist(user.getEmail())) {
			log.error("Throwing UserInvalidException from validateNewUser(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
            throw new UserInvalidException(ApiMessages.USER_EMAIL_ALREADY_EXISTS_ERROR.getMessage());
        }
        if (doesPhoneNumberExist(user.getPhoneNumber())) {
			log.error("Throwing UserInvalidException from validateNewUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
            throw new UserInvalidException(ApiMessages.USER_PHONE_NUMBER_ALREADY_EXISTS_ERROR.getMessage());
        }
		log.debug("<<< Exiting validateNewUser(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
    }

    public boolean doesAccountExist(String accountNumber) {
		log.debug(">>> Entering doesAccountExist(accountNumber={})", accountNumber);
		long start = System.currentTimeMillis();
        boolean result = userRepository.findByAccountAccountNumber(accountNumber).isPresent();
		log.info("doesAccountExist(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
		log.debug("<<< Exiting doesAccountExist(accountNumber={}) -> {}", accountNumber, result);
        return result;
    }

    public boolean doesEmailExist(String email) {
		log.debug(">>> Entering doesEmailExist(email={})", email);
		long start = System.currentTimeMillis();
        boolean result = userRepository.findByEmail(email).isPresent();
		log.info("doesEmailExist(email)={}: find query executed in {} ms", email, (System.currentTimeMillis() - start));
		log.debug("<<< Exiting doesEmailExist(email={}) -> {}", email, result);
        return result;
    }

    public boolean doesPhoneNumberExist(String phoneNumber) {
		log.debug(">>> Entering doesPhoneNumberExist(phoneNumber={})", phoneNumber);
		long start = System.currentTimeMillis();
        boolean result = userRepository.findByPhoneNumber(phoneNumber).isPresent();
		log.info("doesPhoneNumberExist(phoneNumber)={}: find query executed in {} ms", phoneNumber, (System.currentTimeMillis() - start));
		log.debug("<<< Exiting doesPhoneNumberExist(phoneNumber={}) -> {}", phoneNumber, result);
        return result;
    }

}
