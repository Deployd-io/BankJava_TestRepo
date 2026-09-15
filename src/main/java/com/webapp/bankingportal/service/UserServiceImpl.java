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
		log.info(">>> Entering registerUser(user={})", user);
        validationUtil.validateNewUser(user);
        encodePassword(user);
        val savedUser = saveUserWithAccount(user);
		log.error("registerUser(user={}): savedUser → {}", user, savedUser);
        ResponseEntity<String> result = ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
		log.info("<<< Exiting registerUser(user={}) -> {}", user, result);
        return result;
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
		log.info(">>> Entering generateOtp(otpRequest=<redacted>)");
        val user = getUserByIdentifier(otpRequest.identifier());
		log.error("generateOtp(otpRequest=<redacted>): user → {}", user);
        val otp = otpService.generateOTP(user.getAccount().getAccountNumber());
		log.error("generateOtp(otpRequest=<redacted>): otp → {}", otp);
        ResponseEntity<String> result = sendOtpEmail(user, otp);
		log.info("<<< Exiting generateOtp(otpRequest=<redacted>)");
        return result;
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
		log.info(">>> Entering updateUser(updatedUser={})", updatedUser);
        val accountNumber = LoggedinUser.getAccountNumber();
        authenticateUser(accountNumber, updatedUser.getPassword());
		log.error("updateUser(updatedUser={}): accountNumber → {}", updatedUser, accountNumber);
        val existingUser = getUserByAccountNumber(accountNumber);
		log.error("updateUser(updatedUser={}): existingUser → {}", updatedUser, existingUser);
        updateUserDetails(existingUser, updatedUser);
        val savedUser = saveUser(existingUser);
		log.error("updateUser(updatedUser={}): savedUser → {}", updatedUser, savedUser);
        ResponseEntity<String> result = ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
		log.info("<<< Exiting updateUser(updatedUser={}) -> {}", updatedUser, result);
        return result;
    }

    @Override
    @Transactional
    public boolean resetPassword(User user, String newPassword) {
		log.info(">>> Entering resetPassword(user={},newPassword=<redacted>)", user);
        try {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
			log.error("resetPassword(user={},newPassword=<redacted>): user → {}", user, user);
			log.info("<<< Exiting resetPassword(user={},newPassword=<redacted>)", user);
            return true;
        } catch (Exception e) {
            throw new PasswordResetException(ApiMessages.PASSWORD_RESET_FAILURE.getMessage(), e);
        }
    }

    @Override
    public ModelAndView logout(String token) throws InvalidTokenException {
		log.info(">>> Entering logout(token=<redacted>)");
        token = token.substring(7);
        tokenService.validateToken(token);
        tokenService.invalidateToken(token);

        log.info("User logged out successfully {}", tokenService.getUsernameFromToken(token));

        ModelAndView result = new ModelAndView("redirect:/logout");
		log.info("<<< Exiting logout(token=<redacted>)");
        return result;
    }

    @Override
    public User saveUser(User user) {
		log.info(">>> Entering saveUser(user={})", user);
        User result = userRepository.save(user);
		log.info("<<< Exiting saveUser(user={}) -> {}", user, result);
        return result;
    }

    @Override
    public User getUserByIdentifier(String identifier) {
		log.info(">>> Entering getUserByIdentifier(identifier={})", identifier);
        User user = null;

		log.error("getUserByIdentifier(identifier={}): user → {}", identifier, user);
        if (validationUtil.doesEmailExist(identifier)) {
            user = getUserByEmail(identifier);
        } else if (validationUtil.doesAccountExist(identifier)) {
            user = getUserByAccountNumber(identifier);
        } else {
			log.debug("Throwing UserInvalidException from getUserByIdentifier(identifier={})", identifier);
		log.info("<<< Exiting getUserByIdentifier(identifier={})", identifier);
            throw new UserInvalidException(
                    String.format(ApiMessages.USER_NOT_FOUND_BY_IDENTIFIER.getMessage(), identifier));
        }

        return user;
    }

    @Override
    public User getUserByAccountNumber(String accountNo) {
		log.info(">>> Entering getUserByAccountNumber(accountNo={})", accountNo);
        return userRepository.findByAccountAccountNumber(accountNo).orElseThrow(
                () -> new UserInvalidException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNo)));
    }

    @Override
    public User getUserByEmail(String email) {
		log.info(">>> Entering getUserByEmail(email={})", email);
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserInvalidException(String.format(ApiMessages.USER_NOT_FOUND_BY_EMAIL.getMessage(), email)));
    }

    private void encodePassword(User user) {
		log.info(">>> Entering encodePassword(user={})", user);
		log.error("encodePassword(user={}): user → {}", user, user);
        user.setCountryCode(user.getCountryCode().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
		log.info("<<< Exiting encodePassword(user={})", user);
    }

    private User saveUserWithAccount(User user) {
		log.info(">>> Entering saveUserWithAccount(user={})", user);
        val savedUser = saveUser(user);
        savedUser.setAccount(accountService.createAccount(savedUser));
		log.error("saveUserWithAccount(user={}): savedUser → {}", user, savedUser);
        User result = saveUser(savedUser);
		log.info("<<< Exiting saveUserWithAccount(user={}) -> {}", user, result);
        return result;
    }

    private User authenticateUser(LoginRequest loginRequest) {
		log.info(">>> Entering authenticateUser(loginRequest={})", loginRequest);
        val user = getUserByIdentifier(loginRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
		log.error("authenticateUser(loginRequest={}): accountNumber → {}", loginRequest, accountNumber);
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(accountNumber, loginRequest.password()));
				log.error("authenticateUser(loginRequest={}): user → {}", loginRequest, user);
		log.info("<<< Exiting authenticateUser(loginRequest={})", loginRequest);
        return user;
    }

    private void authenticateUser(String accountNumber, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(accountNumber, password));
    }

    private String generateAndSaveToken(String accountNumber) throws InvalidTokenException {
		log.info(">>> Entering generateAndSaveToken(accountNumber={})", accountNumber);
        val userDetails = userDetailsService.loadUserByUsername(accountNumber);
        val token = tokenService.generateToken(userDetails);
		log.error("generateAndSaveToken(accountNumber={}): token → {}", accountNumber, token);
		log.error("generateAndSaveToken(accountNumber={}): userDetails → {}", accountNumber, userDetails);
        tokenService.saveToken(token);
		log.info("<<< Exiting generateAndSaveToken(accountNumber={})", accountNumber);
        return token;
    }

    private ResponseEntity<String> sendOtpEmail(User user, String otp) {
		log.info(">>> Entering sendOtpEmail(user={},otp=<redacted>)", user);
        val emailSendingFuture = otpService.sendOTPByEmail(
                user.getEmail(), user.getName(), user.getAccount().getAccountNumber(), otp);

        ResponseEntity<String> successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        ResponseEntity<String> failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

        ResponseEntity<String> logriteResult = emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
		log.info("<<< Exiting sendOtpEmail(user={},otp=<redacted>)", user);
        return logriteResult;
    }

    private void validateOtpRequest(OtpVerificationRequest request) {
		log.info(">>> Entering validateOtpRequest(request={})", request);
        if (request.identifier() == null || request.identifier().isEmpty()) {
			log.debug("Throwing IllegalArgumentException from validateOtpRequest(request={})", request);
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (request.otp() == null || request.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
		log.info("<<< Exiting validateOtpRequest(request={})", request);
    }

    private void validateOtp(User user, String otp) {
		log.info(">>> Entering validateOtp(user={},otp=<redacted>)", user);
        if (!otpService.validateOTP(user.getAccount().getAccountNumber(), otp)) {
			log.debug("Throwing UnauthorizedException from validateOtp(user={},otp=<redacted>)", user);
            throw new UnauthorizedException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
		log.info("<<< Exiting validateOtp(user={},otp=<redacted>)", user);
    }

    private void updateUserDetails(User existingUser, User updatedUser) {
		log.info(">>> Entering updateUserDetails(existingUser={},updatedUser={})", existingUser, updatedUser);
		long start = System.currentTimeMillis();
        ValidationUtil.validateUserDetails(updatedUser);
        updatedUser.setPassword(existingUser.getPassword());
		log.error("updateUserDetails(existingUser={},updatedUser={}): updatedUser → {}", existingUser, updatedUser, updatedUser);
        userMapper.updateUser(updatedUser, existingUser);
		log.warn("updateUserDetails(existingUser,updatedUser)={},{}: update query executed in {} ms", existingUser, updatedUser, (System.currentTimeMillis() - start));
    log.info("<<< Exiting updateUserDetails(existingUser={},updatedUser={})", existingUser, updatedUser);
    }

    private CompletableFuture<Boolean> sendLoginNotification(User user, String ip) {
		log.info(">>> Entering sendLoginNotification(user={},ip={})", user, ip);
        val loginTime = new Timestamp(System.currentTimeMillis()).toString();

		log.error("sendLoginNotification(user={},ip={}): loginTime → {}", user, ip, loginTime);
		log.info("<<< Exiting sendLoginNotification(user={},ip={})", user, ip);
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
		log.info(">>> Entering sendLoginEmail(user={},loginTime={},loginLocation={})", user, loginTime, loginLocation);
        val emailText = emailService.getLoginEmailTemplate(user.getName(), loginTime, loginLocation);
		log.error("sendLoginEmail(user={},loginTime={},loginLocation={}): emailText → {}", user, loginTime, loginLocation, emailText);
        CompletableFuture<Boolean> logriteResult = emailService.sendEmail(user.getEmail(), ApiMessages.EMAIL_SUBJECT_LOGIN.getMessage(), emailText)
                .thenApplyAsync(result -> true)
                .exceptionally(ex -> false);
		log.info("<<< Exiting sendLoginEmail(user={},loginTime={},loginLocation={}) -> {}", user, loginTime, loginLocation, logriteResult);
        return logriteResult;
    }

}
