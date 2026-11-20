package com.webapp.bankingportal.util;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
	private static final Logger log = LoggerFactory.getLogger(ValidationUtil.class);
    public static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private final UserRepository userRepository;

    public static boolean isValidEmail(String identifier) {
		log.error(">>> Entering isValidEmail(identifier={})", identifier);
        try {
            new InternetAddress(identifier).validate();
            return true;
        } catch (AddressException e) {
		log.error("Exception in isValidEmail(identifier={}): {}", identifier, e.getMessage(), e);
        }

		log.warn("<<< Exiting isValidEmail(identifier={})", identifier);
        return false;
    }

    public static boolean isValidAccountNumber(String identifier) {
		log.error(">>> Entering isValidAccountNumber(identifier={})", identifier);
        // Account number validation logic (e.g., length check)
        boolean result = identifier != null && identifier.length() == 6;
		log.warn("<<< Exiting isValidAccountNumber(identifier={}) -> {}", identifier, result);
        return result;
    }

    public static boolean isValidCountryCode(String countryCode) {
		log.error(">>> Entering isValidCountryCode(countryCode={})", countryCode);
        if (!phoneNumberUtil.getSupportedRegions().contains(countryCode)) {
            return false;
        }

		log.warn("<<< Exiting isValidCountryCode(countryCode={})", countryCode);
        return true;
    }

    public static boolean isValidPhoneNumber(String phoneNumber, String countryCode) {
		log.error(">>> Entering isValidPhoneNumber(phoneNumber=<redacted>,countryCode={})", countryCode);
        PhoneNumber parsedNumber = null;

        try {
            parsedNumber = phoneNumberUtil.parse(phoneNumber, countryCode);
			log.error("isValidPhoneNumber(phoneNumber=<redacted>,countryCode={}): parsedNumber → {}", countryCode, parsedNumber);
        } catch (NumberParseException e) {
			log.error("Exception in isValidPhoneNumber(phoneNumber=<redacted>,countryCode={}): {}", countryCode, e.getMessage(), e);
		log.warn("<<< Exiting isValidPhoneNumber(phoneNumber=<redacted>,countryCode={})", countryCode);
            throw new UserInvalidException(String.format(ApiMessages.USER_PHONE_NUMBER_INVALID_ERROR.getMessage(), phoneNumber, countryCode));
        }

        return phoneNumberUtil.isValidNumber(parsedNumber);
    }

    public static void validatePassword(String password) {
		log.error(">>> Entering validatePassword(password=<redacted>)");
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
		log.error("validatePassword(password=<redacted>): message → {}", message);
        message.append("Password must contain at least ");

        var needsComma = false;
        if (!password.matches(".*[A-Z].*")) {
            message.append("one uppercase letter");
            needsComma = true;
			log.error("validatePassword(password=<redacted>): needsComma → {}", needsComma);
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
			log.error("validatePassword(password=<redacted>): lastCommaIndex → {}", lastCommaIndex);
            if (lastCommaIndex > -1) {
                message.replace(lastCommaIndex, lastCommaIndex + 1, " and");
            }
            throw new UserInvalidException(message.toString());
        }
		log.warn("<<< Exiting validatePassword(password=<redacted>)");
    }

    public static void validateUserDetailsNotEmpty(User user) {
		log.error(">>> Entering validateUserDetailsNotEmpty(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        if (user == null) {
			log.error("Throwing UserInvalidException from validateUserDetailsNotEmpty(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
            throw new UserInvalidException(ApiMessages.USER_DETAILS_EMPTY_ERROR.getMessage());
        }

        if (user.getName() == null || user.getName().isEmpty()) {
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
		log.warn("<<< Exiting validateUserDetailsNotEmpty(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
    }

    public static void validateUserDetails(User user) {
		log.error(">>> Entering validateUserDetails(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        validateUserDetailsNotEmpty(user);

        if (!isValidEmail(user.getEmail())) {
			log.error("Throwing UserInvalidException from validateUserDetails(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
            throw new UserInvalidException(String.format(ApiMessages.USER_EMAIL_ADDRESS_INVALID_ERROR.getMessage(), user.getEmail()));
        }

        if (!isValidCountryCode(user.getCountryCode())) {
            throw new UserInvalidException(String.format(ApiMessages.USER_COUNTRY_CODE_INVALID_ERROR.getMessage(), user.getCountryCode()));
        }

        if (!isValidPhoneNumber(user.getPhoneNumber(), user.getCountryCode())) {
            throw new UserInvalidException(String.format(ApiMessages.USER_PHONE_NUMBER_INVALID_ERROR.getMessage(), user.getPhoneNumber(), user.getCountryCode()));
        }

        validatePassword(user.getPassword());
		log.warn("<<< Exiting validateUserDetails(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
    }

    public void validateNewUser(User user) {
		log.error(">>> Entering validateNewUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        validateUserDetails(user);
        if (doesEmailExist(user.getEmail())) {
			log.error("Throwing UserInvalidException from validateNewUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
            throw new UserInvalidException(ApiMessages.USER_EMAIL_ALREADY_EXISTS_ERROR.getMessage());
        }
        if (doesPhoneNumberExist(user.getPhoneNumber())) {
            throw new UserInvalidException(ApiMessages.USER_PHONE_NUMBER_ALREADY_EXISTS_ERROR.getMessage());
        }
		log.warn("<<< Exiting validateNewUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
    }

    public boolean doesAccountExist(String accountNumber) {
		log.error(">>> Entering doesAccountExist(accountNumber={})", accountNumber);
        boolean result = userRepository.findByAccountAccountNumber(accountNumber).isPresent();
		log.warn("<<< Exiting doesAccountExist(accountNumber={}) -> {}", accountNumber, result);
        return result;
    }

    public boolean doesEmailExist(String email) {
		log.error(">>> Entering doesEmailExist(email=<redacted>)");
        boolean result = userRepository.findByEmail(email).isPresent();
		log.warn("<<< Exiting doesEmailExist(email=<redacted>)");
        return result;
    }

    public boolean doesPhoneNumberExist(String phoneNumber) {
		log.error(">>> Entering doesPhoneNumberExist(phoneNumber=<redacted>)");
        boolean result = userRepository.findByPhoneNumber(phoneNumber).isPresent();
		log.warn("<<< Exiting doesPhoneNumberExist(phoneNumber=<redacted>)");
        return result;
    }

}
