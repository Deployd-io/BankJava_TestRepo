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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	log.debug("setId(id={}): id → {}", id, id);

	log.debug(">>> Entering getId()");
	log.debug(">>> Entering setId(id={})", id);
    @NotEmpty
    private String name;
	log.debug("<<< Exiting setId(id={})", id);
	log.debug("<<< Exiting getId()");
	log.debug("setName(name={}): name → {}", name, name);

    @NotEmpty
    private String password;
	log.debug(">>> Entering setName(name={})", name);
	log.debug(">>> Entering getName()");
	log.debug("<<< Exiting setName(name={})", name);
	log.debug("<<< Exiting getName()");

    @Email
   @NotEmpty
    @Column(unique = true)
	log.debug(">>> Entering setPassword(password={})", password);
	log.debug(">>> Entering getPassword()");
	log.debug("<<< Exiting getPassword()");
	log.debug("<<< Exiting setPassword(password={})", password);
    private String email;

    @NotEmpty
    private String countryCode;
	log.debug("setEmail(email={}): email → {}", email, email);
	log.debug(">>> Entering getEmail()");
	log.debug(">>> Entering setEmail(email={})", email);
	log.debug("<<< Exiting getEmail()");
	log.debug("<<< Exiting setEmail(email={})", email);

    @NotEmpty
    @Column(unique = true)
    private String phoneNumber;

	log.debug(">>> Entering getCountryCode()");
	log.debug(">>> Entering setCountryCode(countryCode={})", countryCode);
	log.debug("<<< Exiting getCountryCode()");
	log.debug("<<< Exiting setCountryCode(countryCode={})", countryCode);
	log.debug(">>> Entering setPhoneNumber(phoneNumber={})", phoneNumber);
	log.debug(">>> Entering getPhoneNumber()");
    @NotEmpty
    private String address;

    // Establishing a one-to-one relationship with the account
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;
	log.debug("<<< Exiting getPhoneNumber()");
	log.debug("<<< Exiting setPhoneNumber(phoneNumber={})", phoneNumber);
	log.debug(">>> Entering setAddress(address={})", address);
	log.debug(">>> Entering getAddress()");
	log.debug("<<< Exiting getAddress()");
	log.debug("<<< Exiting setAddress(address={})", address);

}
