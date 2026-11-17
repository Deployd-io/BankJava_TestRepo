package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor
@Data
public class UserResponse {
private static final Logger log = LoggerFactory.getLogger(UserResponse.class);

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
		log.error(">>> Entering UserResponse(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
		log.debug("UserResponse(user={}): countryCode → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), countryCode);
        this.name = user.getName();
        this.email = user.getEmail();
		log.debug("UserResponse(user={}): email → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), email);
        this.countryCode = user.getCountryCode();
        this.phoneNumber = user.getPhoneNumber();
		log.debug("UserResponse(user={}): phoneNumber → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), phoneNumber);
        this.address = user.getAddress();
		log.debug("UserResponse(user={}): address → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), address);
        this.accountNumber = user.getAccount().getAccountNumber();
		log.debug("UserResponse(user={}): accountNumber → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), accountNumber);
        this.ifscCode = user.getAccount().getIfscCode();
		log.debug("UserResponse(user={}): ifscCode → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), ifscCode);
        this.branch = user.getAccount().getBranch();
		log.debug("UserResponse(user={}): branch → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), branch);
        this.accountType = user.getAccount().getAccountType();
		log.warn("<<< Exiting UserResponse(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
    }

}
