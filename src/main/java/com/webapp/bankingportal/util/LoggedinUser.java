package com.webapp.bankingportal.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.webapp.bankingportal.exception.NotFoundException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class LoggedinUser {

    /**
     * Returns the account number of the currently logged in user. If there is
     * no user logged in, an exception is thrown.
     *
     * @return The account number of the currently logged in user.
     * @throws NotFoundException If there is no user logged in.
	log.debug(">>> Entering getAccountNumber()");
     */
	log.debug("getAccountNumber(): authentication → {}", authentication);
    public static String getAccountNumber() {
		log.debug(">>> Entering getAccountNumber()");
        val authentication = SecurityContextHolder.getContext().getAuthentication();
		log.debug("getAccountNumber(): authentication → {}", authentication);
		log.debug("getAccountNumber(): principal → {}", principal);
        if (authentication == null) {
			log.debug("<<< Exiting getAccountNumber()");
            throw new NotFoundException("No user is currently logged in.");
        }
        val principal = (User) authentication.getPrincipal();
		log.debug("getAccountNumber(): principal → {}", principal);
		log.debug("<<< Exiting getAccountNumber()");
        return principal.getUsername();
    }

}
