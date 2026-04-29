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
        val authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new NotFoundException("No user is currently logged in.");
        }
        val principal = (User) authentication.getPrincipal();
		log.info("getAccountNumber()=null): SOC2-AUDIT [CC6.1]: Authentication operation completed");
        return principal.getUsername();
    }

}
