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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
private static final Logger log = LoggerFactory.getLogger(UserController.class);
@Autowired
    private final UserService userService;
@Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
		log.error(">>> Entering registerUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        ResponseEntity<String> response = userService.registerUser(user);

		log.error("registerUser(user={}): response → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), response);
        // Only send email if registration succeeded
        if (response.getStatusCode().is2xxSuccessful()) {
            String emailBody = emailService.getBankStatementEmailTemplate(user.getName(), "Welcome! Your account is created.");
			log.error("registerUser(user={}): emailBody → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), emailBody);
            emailService.sendEmail(user.getEmail(), "Welcome to OneStopBank", emailBody);
        }

		log.warn("<<< Exiting registerUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
			log.error(">>> Entering login(loginRequest={},request={})", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"), request);

        ResponseEntity<String> result = userService.login(loginRequest, request);
		log.warn("<<< Exiting login(loginRequest={},request={}) -> {}", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"), request, result);
        return result;
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
		log.error(">>> Entering generateOtp(otpRequest={})", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        ResponseEntity<String> result = userService.generateOtp(otpRequest);
		log.warn("<<< Exiting generateOtp(otpRequest={})", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        return result;
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
			log.error(">>> Entering verifyOtpAndLogin(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));

        ResponseEntity<String> result = userService.verifyOtpAndLogin(otpVerificationRequest);
		log.warn("<<< Exiting verifyOtpAndLogin(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        return result;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
		log.error(">>> Entering updateUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        ResponseEntity<String> result = userService.updateUser(user);
		log.warn("<<< Exiting updateUser(user={}) -> {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), result);
        return result;
    }

    @GetMapping("/logout")
    public ModelAndView logout(@RequestHeader("Authorization") String token)
            throws InvalidTokenException {
			log.error(">>> Entering logout(token=<redacted>)");

        ModelAndView result = userService.logout(token);
		log.warn("<<< Exiting logout(token=<redacted>)");
        return result;
    }

}
