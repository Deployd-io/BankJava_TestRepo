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
		log.debug(">>> Entering getAccountNumber()");
        val authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
			log.error("Throwing NotFoundException from getAccountNumber()");
			log.debug("getAccountNumber(): authentication → {}", authentication);
            throw new NotFoundException("No user is currently logged in.");
        }
        val principal = (User) authentication.getPrincipal();
		log.debug("getAccountNumber(): principal → {}", principal);
        String result = principal.getUsername();
		log.debug("<<< Exiting getAccountNumber() -> {}", result);
        return result;
    }

}
