package com.webapp.bankingportal.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.webapp.bankingportal.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import lombok.val;

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
		log.debug("<<< Exiting getAccountNumber()");
        val authentication = SecurityContextHolder.getContext().getAuthentication();
		log.debug(">>> Entering getAccountNumber()");
		log.debug("getAccountNumber(): authentication → {}", authentication);
        if (authentication == null) {
            throw new NotFoundException("No user is currently logged in.");
			log.debug("<<< Exiting NotFoundException(null={})", null);
			log.debug(">>> Entering NotFoundException(null={})", null);
        }
        val principal = (User) authentication.getPrincipal();
        return principal.getUsername();
    }

}
