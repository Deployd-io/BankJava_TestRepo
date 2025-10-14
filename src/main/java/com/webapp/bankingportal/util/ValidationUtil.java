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
        try {
			log.debug(">>> Entering isValidEmail(identifier={})", identifier);
            new InternetAddress(identifier).validate();
            return true;
        } catch (AddressException e) {
            log.warn("Invalid email address: {}", identifier);
        }

		log.debug("<<< Exiting isValidEmail(identifier={})", identifier);
        return false;
    }

    public static boolean isValidAccountNumber(String identifier) {
        // Account number validation logic (e.g., length check)
		log.debug(">>> Entering isValidAccountNumber(identifier={})", identifier);
		log.debug("<<< Exiting isValidAccountNumber(identifier={})", identifier);
        return identifier != null && identifier.length() == 6;
    }

    public static boolean isValidCountryCode(String countryCode) {
        if (!phoneNumberUtil.getSupportedRegions().contains(countryCode)) {
			log.debug(">>> Entering isValidCountryCode(countryCode={})", countryCode);
            return false;
        }

		log.debug("<<< Exiting isValidCountryCode(countryCode={})", countryCode);
        return true;
    }

    public static boolean isValidPhoneNumber(String phoneNumber, String countryCode) {
        PhoneNumber parsedNumber = null;
		log.debug(">>> Entering isValidPhoneNumber(phoneNumber={},countryCode={})", phoneNumber, countryCode);

        try {
            parsedNumber = phoneNumberUtil.parse(phoneNumber, countryCode);
			log.debug("isValidPhoneNumber(phoneNumber={},countryCode={}): parsedNumber → {}", phoneNumber, countryCode, parsedNumber);
        } catch (NumberParseException e) {
			log.error("Exception in isValidPhoneNumber(phoneNumber={},countryCode={}): {}", phoneNumber, countryCode, e.getMessage(), e);
            throw new UserInvalidException(String.format(ApiMessages.USER_PHONE_NUMBER_INVALID_ERROR.getMessage(), phoneNumber, countryCode));
        }

		log.debug("<<< Exiting isValidPhoneNumber(phoneNumber={},countryCode={})", phoneNumber, countryCode);
        return phoneNumberUtil.isValidNumber(parsedNumber);
    }

    public static void validatePassword(String password) {
        if (password.length() < 8) {
			log.debug(">>> Entering validatePassword(password={})", password);
            throw new UserInvalidException(ApiMessages.PASSWORD_TOO_SHORT_ERROR.getMessage());
        }

        if (password.length() >= 128) {
            throw new UserInvalidException(ApiMessages.PASSWORD_TOO_LONG_ERROR.getMessage());
        }

        if (password.matches(".*\\s.*")) {
            throw new UserInvalidException(ApiMessages.PASSWORD_CONTAINS_WHITESPACE_ERROR.getMessage());
        }

        val message = new StringBuilder();
		log.debug("validatePassword(password={}): message → {}", password, message);
        message.append("Password must contain at least ");

        var needsComma = false;
        if (!password.matches(".*[A-Z].*")) {
            message.append("one uppercase letter");
            needsComma = true;
			log.debug("validatePassword(password={}): needsComma → {}", password, needsComma);
        }

		log.debug("<<< Exiting validatePassword(password={})", password);
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
            if (lastCommaIndex > -1) {
                message.replace(lastCommaIndex, lastCommaIndex + 1, " and");
            }
            throw new UserInvalidException(message.toString());
        }
    }

    public static void validateUserDetailsNotEmpty(User user) {
        if (user == null) {
			log.debug(">>> Entering validateUserDetailsNotEmpty(user={})", user);
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
		log.debug("<<< Exiting validateUserDetailsNotEmpty(user={})", user);
    }

    public static void validateUserDetails(User user) {
        validateUserDetailsNotEmpty(user);
		log.debug(">>> Entering validateUserDetails(user={})", user);

		log.debug("<<< Exiting validateUserDetails(user={})", user);
        if (!isValidEmail(user.getEmail())) {
            throw new UserInvalidException(String.format(ApiMessages.USER_EMAIL_ADDRESS_INVALID_ERROR.getMessage(), user.getEmail()));
        }

        if (!isValidCountryCode(user.getCountryCode())) {
            throw new UserInvalidException(String.format(ApiMessages.USER_COUNTRY_CODE_INVALID_ERROR.getMessage(), user.getCountryCode()));
        }

        if (!isValidPhoneNumber(user.getPhoneNumber(), user.getCountryCode())) {
            throw new UserInvalidException(String.format(ApiMessages.USER_PHONE_NUMBER_INVALID_ERROR.getMessage(), user.getPhoneNumber(), user.getCountryCode()));
        }

        validatePassword(user.getPassword());
    }

    public void validateNewUser(User user) {
        validateUserDetails(user);
		log.debug(">>> Entering validateNewUser(user={})", user);
        if (doesEmailExist(user.getEmail())) {
			log.debug("<<< Exiting validateNewUser(user={})", user);
            throw new UserInvalidException(ApiMessages.USER_EMAIL_ALREADY_EXISTS_ERROR.getMessage());
        }
        if (doesPhoneNumberExist(user.getPhoneNumber())) {
            throw new UserInvalidException(ApiMessages.USER_PHONE_NUMBER_ALREADY_EXISTS_ERROR.getMessage());
        }
    }

    public boolean doesAccountExist(String accountNumber) {
		log.debug(">>> Entering doesAccountExist(accountNumber={})", accountNumber);
		log.debug("<<< Exiting doesAccountExist(accountNumber={})", accountNumber);
        return userRepository.findByAccountAccountNumber(accountNumber).isPresent();
    }

    public boolean doesEmailExist(String email) {
		log.debug(">>> Entering doesEmailExist(email={})", email);
		log.debug("<<< Exiting doesEmailExist(email={})", email);
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean doesPhoneNumberExist(String phoneNumber) {
		log.debug(">>> Entering doesPhoneNumberExist(phoneNumber={})", phoneNumber);
		log.debug("<<< Exiting doesPhoneNumberExist(phoneNumber={})", phoneNumber);
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

}
