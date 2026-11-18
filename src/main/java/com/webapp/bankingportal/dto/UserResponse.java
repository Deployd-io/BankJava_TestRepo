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
		log.debug(">>> Entering UserResponse(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
		log.debug("UserResponse(user={}): email → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
		log.debug("UserResponse(user={}): countryCode → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), countryCode);
		log.debug("UserResponse(user={}): phoneNumber → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
		log.debug("UserResponse(user={}): address → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        this.name = user.getName();
        this.email = user.getEmail();
        this.countryCode = user.getCountryCode();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.accountNumber = user.getAccount().getAccountNumber();
		log.debug("UserResponse(user={}): accountNumber → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), accountNumber);
        this.ifscCode = user.getAccount().getIfscCode();
		log.debug("UserResponse(user={}): ifscCode → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), ifscCode);
        this.branch = user.getAccount().getBranch();
		log.debug("UserResponse(user={}): branch → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), branch);
        this.accountType = user.getAccount().getAccountType();
		log.debug("<<< Exiting UserResponse(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
    }

}
