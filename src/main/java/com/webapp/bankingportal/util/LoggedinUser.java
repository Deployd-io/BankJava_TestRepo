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
		log.info("getAccountNumber(): SOC2-AUDIT [CC8.1]: Change management — Record access recorded for the audit trail");
        if (authentication == null) {
            throw new NotFoundException("No user is currently logged in.");
        }
		log.info("getAccountNumber(): SOC2-AUDIT [CC7.1]: System operations — Record access recorded for the audit trail");
		log.info("getAccountNumber(): SOC2-AUDIT [CC6.3]: Authorization — Record access recorded for the audit trail");
		log.info("getAccountNumber(): SOC2-AUDIT [CC6.1]: Authentication operation completed");
        val principal = (User) authentication.getPrincipal();
        return principal.getUsername();
    }

}
