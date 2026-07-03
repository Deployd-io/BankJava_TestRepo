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
public static final Logger log = LoggerFactory.getLogger(UserController.class);
@Autowired
    private final UserService userService;
@Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
		log.info(">>> Entering registerUser(user={})", user);
        ResponseEntity<String> response = userService.registerUser(user);

		log.info("registerUser(user={}): response → {}", user, response);
        // Only send email if registration succeeded
        if (response.getStatusCode().is2xxSuccessful()) {
            String emailBody = emailService.getBankStatementEmailTemplate(user.getName(), "Welcome! Your account is created.");
            emailService.sendEmail(user.getEmail(), "Welcome to OneStopBank", emailBody);
			log.info("registerUser(user={}): emailBody → {}", user, emailBody);
        }

		log.info("<<< Exiting registerUser(user={})", user);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
			log.info(">>> Entering login(loginRequest={},request={})", loginRequest, request);

		log.info("<<< Exiting login(loginRequest={},request={})", loginRequest, request);
        return userService.login(loginRequest, request);
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
		log.info(">>> Entering generateOtp(otpRequest={})", otpRequest);
		log.info("<<< Exiting generateOtp(otpRequest={})", otpRequest);
        return userService.generateOtp(otpRequest);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
			log.info(">>> Entering verifyOtpAndLogin(otpVerificationRequest={})", otpVerificationRequest);

		log.info("<<< Exiting verifyOtpAndLogin(otpVerificationRequest={})", otpVerificationRequest);
        return userService.verifyOtpAndLogin(otpVerificationRequest);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
		log.info(">>> Entering updateUser(user={})", user);
		log.info("<<< Exiting updateUser(user={})", user);
        return userService.updateUser(user);
    }

    @GetMapping("/logout")
    public ModelAndView logout(@RequestHeader("Authorization") String token)
            throws InvalidTokenException {

        return userService.logout(token);
    }

}
