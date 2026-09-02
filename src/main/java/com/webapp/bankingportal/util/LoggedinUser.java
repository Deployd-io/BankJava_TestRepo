package com.webapp.bankingportal.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.webapp.bankingportal.exception.NotFoundException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggedinUser {

    /**
     * Returns the account number of the currently logged in user. If there is
     * no user logged in, an exception is thrown.
     *
     * @return The account number of the currently logged in user.
     * @throws NotFoundException If there is no user logged in.
     */
    public static String getAccountNumber() {
        val authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info("ENTERING: getAccountNumber() method");
        if (authentication == null) {
			log.warn("ENTERING: getAccountNumber() method");
            throw new NotFoundException("No user is currently logged in.");
        }
        val principal = (User) authentication.getPrincipal();
		log.warn("getAccountNumber()=null): GDPR-AUDIT: Record access recorded for the audit trail");
		log.info("getAccountNumber()=null): GDPR-AUDIT [Art.30]: Personal data processing activity completed");
        return principal.getUsername();
    }

}
