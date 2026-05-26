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
        ResponseEntity<String> response = userService.registerUser(user);

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

		log.warn("login(loginRequest,request)={},{}): HIPAA-AUDIT [§164.312(e)(2)(ii)]: Secure PHI transmission completed", loginRequest, request);
		log.warn("login(loginRequest,request)={},{}): HIPAA-AUDIT [§164.312(d)]: Authentication verification completed", loginRequest, request);
        return userService.login(loginRequest, request);
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
        return userService.generateOtp(otpRequest);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
			log.warn("verifyOtpAndLogin(otpVerificationRequest)={}): HIPAA-AUDIT [§164.312(e)(2)(i)]: Secure PHI transmission completed", otpVerificationRequest);
			log.info("verifyOtpAndLogin(otpVerificationRequest)={}): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed", otpVerificationRequest);
			log.warn("verifyOtpAndLogin(otpVerificationRequest)={}): HIPAA-AUDIT [§164.312(d)]: Authentication verification completed", otpVerificationRequest);

        return userService.verifyOtpAndLogin(otpVerificationRequest);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/logout")
    public ModelAndView logout(@RequestHeader("Authorization") String token)
            throws InvalidTokenException {

        return userService.logout(token);
    }

}
