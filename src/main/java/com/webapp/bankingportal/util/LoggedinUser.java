package com.webapp.bankingportal.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.webapp.bankingportal.exception.NotFoundException;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggedinUser {
public static final Logger log = LoggerFactory.getLogger(LoggedinUser.class);

    /**
     * Returns the account number of the currently logged in user. If there is
     * no user logged in, an exception is thrown.
     *
     * @return The account number of the currently logged in user.
     * @throws NotFoundException If there is no user logged in.
     */
    public static String getAccountNumber() {
		log.debug(">>> Entering getAccountNumber()");
        val authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
			log.debug("getAccountNumber(): authentication → {}", authentication);
            throw new NotFoundException("No user is currently logged in.");
        }
        val principal = (User) authentication.getPrincipal();
		log.debug("getAccountNumber(): principal → {}", principal);
		log.debug("<<< Exiting getAccountNumber()");
        return principal.getUsername();
    }

}
