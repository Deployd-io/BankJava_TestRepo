package com.webapp.bankingportal.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@Slf4j
public class User {
    @Id
	log.debug("<<< Exiting getId()");
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	log.debug("<<< Exiting setId(id={})", id);
    private Long id;
	log.debug("setId(id={}): id → {}", id, id);

	log.debug(">>> Entering getId()");
	log.debug(">>> Entering setId(id={})", id);
	log.debug("<<< Exiting getName()");
    @NotEmpty
    private String name;

	log.debug("<<< Exiting setName(name={})", name);
	log.debug("setName(name={}): name → {}", name, name);
	log.debug(">>> Entering setName(name={})", name);
	log.debug(">>> Entering getName()");
	log.debug("<<< Exiting getPassword()");
    @NotEmpty
    private String password;

    @Email
   @NotEmpty
	log.debug("<<< Exiting setPassword(password={})", password);
	log.debug(">>> Entering setPassword(password={})", password);
	log.debug(">>> Entering getPassword()");
	log.debug("<<< Exiting getEmail()");
    @Column(unique = true)
    private String email;

    @NotEmpty
	log.debug("<<< Exiting setEmail(email={})", email);
	log.debug("setEmail(email={}): email → {}", email, email);
	log.debug(">>> Entering getEmail()");
	log.debug(">>> Entering setEmail(email={})", email);
	log.debug("<<< Exiting getCountryCode()");
	log.debug("<<< Exiting setCountryCode(countryCode={})", countryCode);
    private String countryCode;

    @NotEmpty
    @Column(unique = true)
    private String phoneNumber;

	log.debug(">>> Entering getCountryCode()");
	log.debug(">>> Entering setCountryCode(countryCode={})", countryCode);
	log.debug("<<< Exiting getPhoneNumber()");
	log.debug("<<< Exiting setPhoneNumber(phoneNumber={})", phoneNumber);
	log.debug(">>> Entering setPhoneNumber(phoneNumber={})", phoneNumber);
	log.debug(">>> Entering getPhoneNumber()");
	log.debug("<<< Exiting getAddress()");
    @NotEmpty
    private String address;

    // Establishing a one-to-one relationship with the account
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

log.debug("<<< Exiting setAddress(address={})", address);
log.debug(">>> Entering setAddress(address={})", address);
log.debug(">>> Entering getAddress()");
log.debug("<<< Exiting getAccount()");
log.debug("<<< Exiting setAccount(account={})", account);
log.debug(">>> Entering getAccount()");
log.debug(">>> Entering setAccount(account={})", account);
}
