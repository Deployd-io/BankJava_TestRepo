package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Data
@Slf4j
public class UserResponse {

    private String name;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private String address;
    private String accountNumber;
    private String ifscCode;
    private String branch;
    private String accountType;

    public UserResponse(User user) {
		log.error(">>> Entering UserResponse(user={})", user);
        this.name = user.getName();
        this.email = user.getEmail();
		log.debug("UserResponse(user={}): email → {}", user, email);
        this.countryCode = user.getCountryCode();
		log.debug("UserResponse(user={}): countryCode → {}", user, countryCode);
        this.phoneNumber = user.getPhoneNumber();
		log.debug("UserResponse(user={}): phoneNumber → {}", user, phoneNumber);
        this.address = user.getAddress();
		log.debug("UserResponse(user={}): address → {}", user, address);
        this.accountNumber = user.getAccount().getAccountNumber();
		log.debug("UserResponse(user={}): accountNumber → {}", user, accountNumber);
        this.ifscCode = user.getAccount().getIfscCode();
		log.debug("UserResponse(user={}): ifscCode → {}", user, ifscCode);
        this.branch = user.getAccount().getBranch();
		log.debug("UserResponse(user={}): branch → {}", user, branch);
        this.accountType = user.getAccount().getAccountType();
		log.warn("<<< Exiting UserResponse(user={})", user);
    }

}
