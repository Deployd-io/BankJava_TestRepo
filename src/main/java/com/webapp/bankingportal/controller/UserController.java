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
        ResponseEntity<String> response = userService.registerUser(user);
		log.warn("registerUser(user)={}): SOC2-AUDIT: Authentication attempt completed for user {}", user, authResult);

		log.info("registerUser(user)={}): SOC2-AUDIT: System operation completed with status {}", user, operationStatus);
        // Only send email if registration succeeded
        if (response.getStatusCode().is2xxSuccessful()) {
            String emailBody = emailService.getBankStatementEmailTemplate(user.getName(), "Welcome! Your account is created.");
            emailService.sendEmail(user.getEmail(), "Welcome to OneStopBank", emailBody);
        }

        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
			log.warn("login(loginRequest,request)={},{}): SOC2-AUDIT: Authentication attempt completed for user {}", loginRequest, request, authResult);

		log.warn("login(loginRequest,request)={},{}): SOC2-AUDIT: Security incident detected with severity {}", loginRequest, request, incidentSeverity);
        return userService.login(loginRequest, request);
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
        return userService.generateOtp(otpRequest);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
			log.warn("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT: Authentication attempt completed for user {}", otpVerificationRequest, authResult);

		log.warn("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT: Security incident detected with severity {}", otpVerificationRequest, incidentSeverity);
		log.info("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT: Database transaction completed with status {}", otpVerificationRequest, transactionStatus);
        return userService.verifyOtpAndLogin(otpVerificationRequest);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
		log.info("updateUser(user)={}): SOC2-AUDIT: Change management operation performed by initiator {}", user, changeInitiator);
        return userService.updateUser(user);
    }

    @GetMapping("/logout")
    public ModelAndView logout(@RequestHeader("Authorization") String token)
            throws InvalidTokenException {

        return userService.logout(token);
    }

}
