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
		log.info(">>> Entering UserResponse(user={})", user);
        this.name = user.getName();
        this.email = user.getEmail();
		log.error("UserResponse(user={}): email → {}", user, email);
        this.countryCode = user.getCountryCode();
		log.error("UserResponse(user={}): countryCode → {}", user, countryCode);
        this.phoneNumber = user.getPhoneNumber();
		log.error("UserResponse(user={}): phoneNumber → {}", user, phoneNumber);
        this.address = user.getAddress();
		log.error("UserResponse(user={}): address → {}", user, address);
        this.accountNumber = user.getAccount().getAccountNumber();
		log.error("UserResponse(user={}): accountNumber → {}", user, accountNumber);
        this.ifscCode = user.getAccount().getIfscCode();
		log.error("UserResponse(user={}): ifscCode → {}", user, ifscCode);
        this.branch = user.getAccount().getBranch();
		log.error("UserResponse(user={}): branch → {}", user, branch);
        this.accountType = user.getAccount().getAccountType();
		log.info("<<< Exiting UserResponse(user={})", user);
    }

}
