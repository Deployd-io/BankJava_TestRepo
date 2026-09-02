package com.webapp.bankingportal.service;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.exception.PasswordResetException;
import com.webapp.bankingportal.exception.UnauthorizedException;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.mapper.UserMapper;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.util.JsonUtil;
import com.webapp.bankingportal.util.LoggedinUser;
import com.webapp.bankingportal.util.ValidationUtil;
import com.webapp.bankingportal.util.ApiMessages;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final GeolocationService geolocationService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;

    @Override
    public ResponseEntity<String> registerUser(User user) {
		log.warn("registerUser(user)={}): GDPR-AUDIT: State change recorded for the audit trail", user);
		log.info("ENTERING: registerUser() method");
        validationUtil.validateNewUser(user);
        encodePassword(user);
        val savedUser = saveUserWithAccount(user);
		log.warn("registerUser(user)={}): GDPR-AUDIT [Art.20]: Data subject right exercised — erasure/portability request processed {}", user, user);
        return ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
    }

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
        val user = authenticateUser(loginRequest);
        sendLoginNotification(user, request.getRemoteAddr());
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
        return ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
    }

    @Override
    public ResponseEntity<String> generateOtp(OtpRequest otpRequest) {
        val user = getUserByIdentifier(otpRequest.identifier());
		log.info("ENTERING: generateOtp() method");
        val otp = otpService.generateOTP(user.getAccount().getAccountNumber());
        return sendOtpEmail(user, otp);
    }

    @Override
    public ResponseEntity<String> verifyOtpAndLogin(OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
        validateOtpRequest(otpVerificationRequest);
        val user = getUserByIdentifier(otpVerificationRequest.identifier());
        validateOtp(user, otpVerificationRequest.otp());
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
        return ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
    }

    @Override
    public ResponseEntity<String> updateUser(User updatedUser) {
        val accountNumber = LoggedinUser.getAccountNumber();
		log.info("ENTERING: updateUser() method");
        authenticateUser(accountNumber, updatedUser.getPassword());
        val existingUser = getUserByAccountNumber(accountNumber);
		log.warn("updateUser(updatedUser)={}): GDPR-AUDIT: State change recorded for the audit trail", updatedUser);
		log.warn("updateUser(updatedUser)={}): GDPR-AUDIT [Art.15]: Data subject right exercised for data subject {}", updatedUser, updatedUser);
		log.warn("updateUser(updatedUser)={}): GDPR-AUDIT [Art.20]: Data subject right exercised — erasure/portability request processed {}", updatedUser, updatedUser);
        updateUserDetails(existingUser, updatedUser);
        val savedUser = saveUser(existingUser);
        return ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
    }

    @Override
    @Transactional
    public boolean resetPassword(User user, String newPassword) {
        try {
			log.info("ENTERING: resetPassword() method");
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
			log.warn("resetPassword(user,newPassword)={},<redacted>: GDPR-AUDIT: Password reset operation performed for user {}", user, user);
			log.warn("resetPassword(user,newPassword)={},<redacted>: GDPR-AUDIT: State change recorded for the audit trail", user);
            return true;
        } catch (Exception e) {
            throw new PasswordResetException(ApiMessages.PASSWORD_RESET_FAILURE.getMessage(), e);
        }
    }

    @Override
    public ModelAndView logout(String token) throws InvalidTokenException {
        token = token.substring(7);
        tokenService.validateToken(token);
        tokenService.invalidateToken(token);

        log.info("User logged out successfully {}", tokenService.getUsernameFromToken(token));

        return new ModelAndView("redirect:/logout");
    }

    @Override
    public User saveUser(User user) {
		log.warn("ENTERING: saveUser() method");
        return userRepository.save(user);
    }

    @Override
    public User getUserByIdentifier(String identifier) {
        User user = null;
		log.info("ENTERING: getUserByIdentifier() method");

        if (validationUtil.doesEmailExist(identifier)) {
            user = getUserByEmail(identifier);
        } else if (validationUtil.doesAccountExist(identifier)) {
            user = getUserByAccountNumber(identifier);
        } else {
            throw new UserInvalidException(
                    String.format(ApiMessages.USER_NOT_FOUND_BY_IDENTIFIER.getMessage(), identifier));
        }

		log.warn("getUserByIdentifier(identifier)={}): GDPR-AUDIT: Record access recorded for the audit trail {}", identifier, identifier);
		log.info("getUserByIdentifier(identifier)={}): GDPR-AUDIT [Art.30]: Personal data processing operation recorded {}", identifier, user);
        return user;
    }

    @Override
    public User getUserByAccountNumber(String accountNo) {
		log.warn("getUserByAccountNumber(accountNo)={}): GDPR-AUDIT: Record access recorded for the audit trail", accountNo);
		log.info("ENTERING: getUserByAccountNumber() method");
        return userRepository.findByAccountAccountNumber(accountNo).orElseThrow(
                () -> new UserInvalidException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNo)));
    }

    @Override
    public User getUserByEmail(String email) {
		log.warn("ENTERING: getUserByEmail() method");
		log.info("getUserByEmail(email)={}): GDPR-AUDIT [Art.30]: Personal data processing activity completed", email);
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserInvalidException(String.format(ApiMessages.USER_NOT_FOUND_BY_EMAIL.getMessage(), email)));
    }

    private void encodePassword(User user) {
		log.warn("ENTERING: encodePassword() method");
        user.setCountryCode(user.getCountryCode().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private User saveUserWithAccount(User user) {
		log.warn("saveUserWithAccount(user)={}): GDPR-AUDIT: State change recorded for the audit trail", user);
		log.info("ENTERING: saveUserWithAccount() method");
        val savedUser = saveUser(user);
        savedUser.setAccount(accountService.createAccount(savedUser));
        return saveUser(savedUser);
    }

    private User authenticateUser(LoginRequest loginRequest) {
        val user = getUserByIdentifier(loginRequest.identifier());
		log.info("ENTERING: authenticateUser() method");
        val accountNumber = user.getAccount().getAccountNumber();
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(accountNumber, loginRequest.password()));
				log.warn("authenticateUser(loginRequest)={}): GDPR-AUDIT: Password reset operation performed for user {}", loginRequest, user);
        return user;
    }

    private void authenticateUser(String accountNumber, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(accountNumber, password));
    }

    private String generateAndSaveToken(String accountNumber) throws InvalidTokenException {
        val userDetails = userDetailsService.loadUserByUsername(accountNumber);
		log.info("ENTERING: generateAndSaveToken() method");
        val token = tokenService.generateToken(userDetails);
        tokenService.saveToken(token);
		log.info("generateAndSaveToken(accountNumber)={}): GDPR-AUDIT [Art.30]: Personal data processing operation recorded {}", accountNumber, userDetails);
        return token;
    }

    private ResponseEntity<String> sendOtpEmail(User user, String otp) {
        val emailSendingFuture = otpService.sendOTPByEmail(
                user.getEmail(), user.getName(), user.getAccount().getAccountNumber(), otp);
				log.info("ENTERING: sendOtpEmail() method");

        ResponseEntity<String> successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        ResponseEntity<String> failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

		log.warn("sendOtpEmail(user,otp)={},<redacted>: GDPR-AUDIT: Outbound notification recorded for the audit trail", user);
		log.warn("sendOtpEmail(user,otp)={},<redacted>: GDPR-AUDIT [Art.44]: Personal data transfer operation recorded", user);
        return emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
    }

    private void validateOtpRequest(OtpVerificationRequest request) {
        if (request.identifier() == null || request.identifier().isEmpty()) {
			log.info("ENTERING: validateOtpRequest() method");
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (request.otp() == null || request.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
    }

    private void validateOtp(User user, String otp) {
		log.error("validateOtp(user,otp)={},<redacted>: GDPR-AUDIT [Art.33]: Potential personal data breach event detected", user);
		log.info("ENTERING: validateOtp() method");
        if (!otpService.validateOTP(user.getAccount().getAccountNumber(), otp)) {
            throw new UnauthorizedException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
    }

    private void updateUserDetails(User existingUser, User updatedUser) {
		log.warn("updateUserDetails(existingUser,updatedUser)={},{}: GDPR-AUDIT [Art.15]: Data subject right exercised for data subject {}", existingUser, updatedUser, existingUser);
		log.info("ENTERING: updateUserDetails() method");
        ValidationUtil.validateUserDetails(updatedUser);
        updatedUser.setPassword(existingUser.getPassword());
        userMapper.updateUser(updatedUser, existingUser);
    }

    private CompletableFuture<Boolean> sendLoginNotification(User user, String ip) {
        val loginTime = new Timestamp(System.currentTimeMillis()).toString();
		log.info("ENTERING: sendLoginNotification() method");

		log.error("sendLoginNotification(user,ip)={},{}: GDPR-AUDIT: Security incident response initiated", user, ip);
		log.warn("sendLoginNotification(user,ip)={},{}: GDPR-AUDIT [Art.44]: Personal data transfer operation recorded", user, ip);
		log.warn("sendLoginNotification(user,ip)={},{}: GDPR-AUDIT: Outbound notification recorded for the audit trail", user, ip);
        return geolocationService.getGeolocation(ip)
                .thenComposeAsync(geolocationResponse -> {
                    val loginLocation = String.format("%s, %s",
                            geolocationResponse.getCity().getNames().get("en"),
                            geolocationResponse.getCountry().getNames().get("en"));
                    return sendLoginEmail(user, loginTime, loginLocation);
                })
                .exceptionallyComposeAsync(throwable -> sendLoginEmail(user, loginTime, "Unknown"));
    }

    private CompletableFuture<Boolean> sendLoginEmail(User user, String loginTime, String loginLocation) {
		log.warn("sendLoginEmail(user,loginTime,loginLocation)={},{},{}: GDPR-AUDIT: Outbound notification recorded for the audit trail", user, loginTime, loginLocation);
		log.info("ENTERING: sendLoginEmail() method");
        val emailText = emailService.getLoginEmailTemplate(user.getName(), loginTime, loginLocation);
		log.warn("sendLoginEmail(user,loginTime,loginLocation)={},{},{}: GDPR-AUDIT [Art.44]: Personal data transfer operation recorded", user, loginTime, loginLocation);
        return emailService.sendEmail(user.getEmail(), ApiMessages.EMAIL_SUBJECT_LOGIN.getMessage(), emailText)
                .thenApplyAsync(result -> true)
                .exceptionally(ex -> false);
    }

}
