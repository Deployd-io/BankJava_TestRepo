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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
@Slf4j
public class UserController {
@Autowired
    private final UserService userService;
@Autowired
    private EmailService emailService;

	log.debug(">>> Entering registerUser(user={})", user);
    @PostMapping("/register")
	log.debug("registerUser(user={}): response → {}", user, response);
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
		log.debug(">>> Entering registerUser(user={})", user);
        ResponseEntity<String> response = userService.registerUser(user);
		log.debug("registerUser(user={}): response → {}", user, response);
		log.debug("registerUser(user={}): emailBody → {}", user, emailBody);

        // Only send email if registration succeeded
        if (response.getStatusCode().is2xxSuccessful()) {
            String emailBody = emailService.getBankStatementEmailTemplate(user.getName(), "Welcome! Your account is created.");
			log.debug("<<< Exiting registerUser(user={})", user);
			log.debug("registerUser(user={}): emailBody → {}", user, emailBody);
            emailService.sendEmail(user.getEmail(), "Welcome to OneStopBank", emailBody);
        }

		log.debug(">>> Entering login(loginRequest={},request={})", loginRequest, request);
		log.debug("<<< Exiting registerUser(user={})", user);
        return response;
    }
	log.debug("<<< Exiting login(loginRequest={},request={})", loginRequest, request);

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request)
			log.debug(">>> Entering login(loginRequest={},request={})", loginRequest, request);
			log.debug(">>> Entering generateOtp(otpRequest={})", otpRequest);
            throws InvalidTokenException {
			log.debug("<<< Exiting generateOtp(otpRequest={})", otpRequest);

		log.debug("<<< Exiting login(loginRequest={},request={})", loginRequest, request);
        return userService.login(loginRequest, request);
    }
	log.debug(">>> Entering verifyOtpAndLogin(otpVerificationRequest={})", otpVerificationRequest);

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
		log.debug("<<< Exiting verifyOtpAndLogin(otpVerificationRequest={})", otpVerificationRequest);
		log.debug(">>> Entering generateOtp(otpRequest={})", otpRequest);
		log.debug("<<< Exiting generateOtp(otpRequest={})", otpRequest);
        return userService.generateOtp(otpRequest);
    }
	log.debug(">>> Entering updateUser(user={})", user);

	log.debug("<<< Exiting updateUser(user={})", user);
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest)
			log.debug(">>> Entering verifyOtpAndLogin(otpVerificationRequest={})", otpVerificationRequest);
            throws InvalidTokenException {

		log.debug("<<< Exiting verifyOtpAndLogin(otpVerificationRequest={})", otpVerificationRequest);
        return userService.verifyOtpAndLogin(otpVerificationRequest);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
		log.debug(">>> Entering updateUser(user={})", user);
		log.debug("<<< Exiting updateUser(user={})", user);
        return userService.updateUser(user);
    }

    @GetMapping("/logout")
    public ModelAndView logout(@RequestHeader("Authorization") String token)
            throws InvalidTokenException {

        return userService.logout(token);
    }

}
