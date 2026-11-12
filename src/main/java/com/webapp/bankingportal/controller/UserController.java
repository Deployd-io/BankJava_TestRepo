package com.webapp.bankingportal.controller;

import com.webapp.bankingportal.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
@Autowired
    private final UserService userService;
@Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
		log.debug(">>> Entering registerUser(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        ResponseEntity<String> response = userService.registerUser(user);

		log.debug("registerUser(user={}): response → {}", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", response);
        // Only send email if registration succeeded
        if (response.getStatusCode().is2xxSuccessful()) {
            String emailBody = emailService.getBankStatementEmailTemplate(user.getName(), "Welcome! Your account is created.");
			log.debug("registerUser(user={}): emailBody → {}", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", emailBody);
            emailService.sendEmail(user.getEmail(), "Welcome to OneStopBank", emailBody);
        }

		log.debug("<<< Exiting registerUser(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
			log.debug(">>> Entering login(loginRequest={},request={})", "[identifier=" + loginRequest.identifier() + "]", request);

        ResponseEntity<String> result = userService.login(loginRequest, request);
		log.debug("<<< Exiting login(loginRequest={},request={}) -> {}", "[identifier=" + loginRequest.identifier() + "]", request, result);
        return result;
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
		log.debug(">>> Entering generateOtp(otpRequest={})", "[identifier=" + otpRequest.identifier() + "]");
        ResponseEntity<String> result = userService.generateOtp(otpRequest);
		log.debug("<<< Exiting generateOtp(otpRequest={})", "[identifier=" + otpRequest.identifier() + "]");
        return result;
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
			log.debug(">>> Entering verifyOtpAndLogin(otpVerificationRequest={})", "[identifier=" + otpVerificationRequest.identifier() + "]");

        ResponseEntity<String> result = userService.verifyOtpAndLogin(otpVerificationRequest);
		log.debug("<<< Exiting verifyOtpAndLogin(otpVerificationRequest={})", "[identifier=" + otpVerificationRequest.identifier() + "]");
        return result;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
		log.debug(">>> Entering updateUser(user={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        ResponseEntity<String> result = userService.updateUser(user);
		log.debug("<<< Exiting updateUser(user={}) -> {}", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", result);
        return result;
    }

    @GetMapping("/logout")
    public ModelAndView logout(@RequestHeader("Authorization") String token)
            throws InvalidTokenException {
			log.debug(">>> Entering logout(token=<redacted>)");

        ModelAndView result = userService.logout(token);
		log.debug("<<< Exiting logout(token=<redacted>)");
        return result;
    }

}
