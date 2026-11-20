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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
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
		log.error(">>> Entering registerUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        validationUtil.validateNewUser(user);
        encodePassword(user);
        val savedUser = saveUserWithAccount(user);
		log.error("registerUser(user={}): savedUser → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        ResponseEntity<String> result = ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
		log.warn("<<< Exiting registerUser(user={}) -> {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), result);
        return result;
    }

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
			log.error(">>> Entering login(loginRequest={},request={})", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"), request);
        val user = authenticateUser(loginRequest);
        sendLoginNotification(user, request.getRemoteAddr());
		log.error("login(loginRequest={},request={}): user → <redacted>", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"), request);
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
		log.error("login(loginRequest={},request={}): token → <redacted>", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"), request);
        ResponseEntity<String> result = ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
		log.warn("<<< Exiting login(loginRequest={},request={})", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"), request);
        return result;
    }

    @Override
    public ResponseEntity<String> generateOtp(OtpRequest otpRequest) {
		log.error(">>> Entering generateOtp(otpRequest={})", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        val user = getUserByIdentifier(otpRequest.identifier());
		log.error("generateOtp(otpRequest={}): user → <redacted>", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        val otp = otpService.generateOTP(user.getAccount().getAccountNumber());
		log.error("generateOtp(otpRequest={}): otp → <redacted>", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        ResponseEntity<String> result = sendOtpEmail(user, otp);
		log.warn("<<< Exiting generateOtp(otpRequest={})", (otpRequest == null ? "null" : "[identifier=" + otpRequest.identifier() + "]"));
        return result;
    }

    @Override
    public ResponseEntity<String> verifyOtpAndLogin(OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
			log.error(">>> Entering verifyOtpAndLogin(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        validateOtpRequest(otpVerificationRequest);
        val user = getUserByIdentifier(otpVerificationRequest.identifier());
		log.error("verifyOtpAndLogin(otpVerificationRequest={}): user → <redacted>", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        validateOtp(user, otpVerificationRequest.otp());
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
		log.error("verifyOtpAndLogin(otpVerificationRequest={}): token → <redacted>", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        ResponseEntity<String> result = ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
		log.warn("<<< Exiting verifyOtpAndLogin(otpVerificationRequest={})", (otpVerificationRequest == null ? "null" : "[identifier=" + otpVerificationRequest.identifier() + "]"));
        return result;
    }

    @Override
    public ResponseEntity<String> updateUser(User updatedUser) {
		log.error(">>> Entering updateUser(updatedUser={})", (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"));
        val accountNumber = LoggedinUser.getAccountNumber();
        authenticateUser(accountNumber, updatedUser.getPassword());
		log.error("updateUser(updatedUser={}): accountNumber → {}", (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"), accountNumber);
        val existingUser = getUserByAccountNumber(accountNumber);
		log.error("updateUser(updatedUser={}): existingUser → <redacted>", (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"));
        updateUserDetails(existingUser, updatedUser);
        val savedUser = saveUser(existingUser);
		log.error("updateUser(updatedUser={}): savedUser → <redacted>", (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"));
        ResponseEntity<String> result = ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
		log.warn("<<< Exiting updateUser(updatedUser={}) -> {}", (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"), result);
        return result;
    }

    @Override
    @Transactional
    public boolean resetPassword(User user, String newPassword) {
	long start = System.currentTimeMillis();
		log.error(">>> Entering resetPassword(user={},newPassword=<redacted>)", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        try {
            user.setPassword(passwordEncoder.encode(newPassword));
			log.debug("resetPassword(user={},newPassword=<redacted>): user → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
            userRepository.save(user);
			log.error("resetPassword(user,newPassword)={},<redacted>: save query executed in {} ms", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), (System.currentTimeMillis() - start));
			log.warn("<<< Exiting resetPassword(user={},newPassword=<redacted>)", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
            return true;
        } catch (Exception e) {
            throw new PasswordResetException(ApiMessages.PASSWORD_RESET_FAILURE.getMessage(), e);
        }
    }

    @Override
    public ModelAndView logout(String token) throws InvalidTokenException {
		log.error(">>> Entering logout(token=<redacted>)");
        token = token.substring(7);
        tokenService.validateToken(token);
		log.error("logout(token=<redacted>): token → <redacted>");
        tokenService.invalidateToken(token);

        ModelAndView result = new ModelAndView("redirect:/logout");
		log.warn("<<< Exiting logout(token=<redacted>)");
        return result;
    }

    @Override
    public User saveUser(User user) {
		log.error(">>> Entering saveUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        User result = userRepository.save(user);
		log.warn("<<< Exiting saveUser(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        return result;
    }

    @Override
    public User getUserByIdentifier(String identifier) {
		log.error(">>> Entering getUserByIdentifier(identifier={})", identifier);
        User user = null;

        if (validationUtil.doesEmailExist(identifier)) {
            user = getUserByEmail(identifier);
        } else if (validationUtil.doesAccountExist(identifier)) {
            user = getUserByAccountNumber(identifier);
        } else {
			log.error("Throwing UserInvalidException from getUserByIdentifier(identifier={})", identifier);
		log.warn("<<< Exiting getUserByIdentifier(identifier={})", identifier);
            throw new UserInvalidException(
                    String.format(ApiMessages.USER_NOT_FOUND_BY_IDENTIFIER.getMessage(), identifier));
        }

        return user;
    }

    @Override
    public User getUserByAccountNumber(String accountNo) {
		log.error(">>> Entering getUserByAccountNumber(accountNo={})", accountNo);
        return userRepository.findByAccountAccountNumber(accountNo).orElseThrow(
                () -> new UserInvalidException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNo)));
    }

    @Override
    public User getUserByEmail(String email) {
		log.error(">>> Entering getUserByEmail(email=<redacted>)");
        User result = userRepository.findByEmail(email).orElseThrow(
                () -> new UserInvalidException(String.format(ApiMessages.USER_NOT_FOUND_BY_EMAIL.getMessage(), email)));
		log.warn("<<< Exiting getUserByEmail(email=<redacted>)");
        return result;
    }

    private void encodePassword(User user) {
		log.error(">>> Entering encodePassword(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
		log.debug("encodePassword(user={}): user → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        user.setCountryCode(user.getCountryCode().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
		log.warn("<<< Exiting encodePassword(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
    }

    private User saveUserWithAccount(User user) {
		log.error(">>> Entering saveUserWithAccount(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        val savedUser = saveUser(user);
		log.error("saveUserWithAccount(user={}): savedUser → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
		log.debug("saveUserWithAccount(user={}): savedUser → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        savedUser.setAccount(accountService.createAccount(savedUser));
        User result = saveUser(savedUser);
		log.warn("<<< Exiting saveUserWithAccount(user={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        return result;
    }

    private User authenticateUser(LoginRequest loginRequest) {
		log.error(">>> Entering authenticateUser(loginRequest={})", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"));
        val user = getUserByIdentifier(loginRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
		log.error("authenticateUser(loginRequest={}): accountNumber → {}", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"), accountNumber);
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(accountNumber, loginRequest.password()));
				log.error("authenticateUser(loginRequest={}): user → <redacted>", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"));
		log.warn("<<< Exiting authenticateUser(loginRequest={})", (loginRequest == null ? "null" : "[identifier=" + loginRequest.identifier() + "]"));
        return user;
    }

    private void authenticateUser(String accountNumber, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(accountNumber, password));
    }

    private String generateAndSaveToken(String accountNumber) throws InvalidTokenException {
		log.error(">>> Entering generateAndSaveToken(accountNumber={})", accountNumber);
        val userDetails = userDetailsService.loadUserByUsername(accountNumber);
        val token = tokenService.generateToken(userDetails);
		log.error("generateAndSaveToken(accountNumber={}): token → <redacted>", accountNumber);
		log.error("generateAndSaveToken(accountNumber={}): userDetails → {}", accountNumber, userDetails);
        tokenService.saveToken(token);
		log.warn("<<< Exiting generateAndSaveToken(accountNumber={})", accountNumber);
        return token;
    }

    private ResponseEntity<String> sendOtpEmail(User user, String otp) {
		log.error(">>> Entering sendOtpEmail(user={},otp=<redacted>)", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        val emailSendingFuture = otpService.sendOTPByEmail(
                user.getEmail(), user.getName(), user.getAccount().getAccountNumber(), otp);

        ResponseEntity<String> successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        ResponseEntity<String> failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

        ResponseEntity<String> logriteResult = emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
		log.warn("<<< Exiting sendOtpEmail(user={},otp=<redacted>)", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        return logriteResult;
    }

    private void validateOtpRequest(OtpVerificationRequest request) {
		log.error(">>> Entering validateOtpRequest(request={})", (request == null ? "null" : "[identifier=" + request.identifier() + "]"));
        if (request.identifier() == null || request.identifier().isEmpty()) {
			log.error("Throwing IllegalArgumentException from validateOtpRequest(request={})", (request == null ? "null" : "[identifier=" + request.identifier() + "]"));
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (request.otp() == null || request.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
		log.warn("<<< Exiting validateOtpRequest(request={})", (request == null ? "null" : "[identifier=" + request.identifier() + "]"));
    }

    private void validateOtp(User user, String otp) {
		log.error(">>> Entering validateOtp(user={},otp=<redacted>)", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
        if (!otpService.validateOTP(user.getAccount().getAccountNumber(), otp)) {
			log.error("Throwing UnauthorizedException from validateOtp(user={},otp=<redacted>)", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
            throw new UnauthorizedException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
		log.warn("<<< Exiting validateOtp(user={},otp=<redacted>)", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"));
    }

    private void updateUserDetails(User existingUser, User updatedUser) {
		log.error(">>> Entering updateUserDetails(existingUser={},updatedUser={})", (existingUser == null ? "null" : "[id=" + existingUser.getId() + ", name=" + existingUser.getName() + ", countryCode=" + existingUser.getCountryCode() + "]"), (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"));
		log.debug("updateUserDetails(existingUser={},updatedUser={}): updatedUser → {}", (existingUser == null ? "null" : "[id=" + existingUser.getId() + ", name=" + existingUser.getName() + ", countryCode=" + existingUser.getCountryCode() + "]"), (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"), (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"));
		long start = System.currentTimeMillis();
        ValidationUtil.validateUserDetails(updatedUser);
        updatedUser.setPassword(existingUser.getPassword());
        userMapper.updateUser(updatedUser, existingUser);
		log.error("updateUserDetails(existingUser,updatedUser)={},{}: update query executed in {} ms", (existingUser == null ? "null" : "[id=" + existingUser.getId() + ", name=" + existingUser.getName() + ", countryCode=" + existingUser.getCountryCode() + "]"), (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"), (System.currentTimeMillis() - start));
    log.warn("<<< Exiting updateUserDetails(existingUser={},updatedUser={})", (existingUser == null ? "null" : "[id=" + existingUser.getId() + ", name=" + existingUser.getName() + ", countryCode=" + existingUser.getCountryCode() + "]"), (updatedUser == null ? "null" : "[id=" + updatedUser.getId() + ", name=" + updatedUser.getName() + ", countryCode=" + updatedUser.getCountryCode() + "]"));
    }

    private CompletableFuture<Boolean> sendLoginNotification(User user, String ip) {
		log.error(">>> Entering sendLoginNotification(user={},ip={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), ip);
        val loginTime = new Timestamp(System.currentTimeMillis()).toString();

		log.error("sendLoginNotification(user={},ip={}): loginTime → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), ip, loginTime);
		log.warn("<<< Exiting sendLoginNotification(user={},ip={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), ip);
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
		log.error(">>> Entering sendLoginEmail(user={},loginTime={},loginLocation={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), loginTime, loginLocation);
        val emailText = emailService.getLoginEmailTemplate(user.getName(), loginTime, loginLocation);
		log.error("sendLoginEmail(user={},loginTime={},loginLocation={}): emailText → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), loginTime, loginLocation);
        CompletableFuture<Boolean> logriteResult = emailService.sendEmail(user.getEmail(), ApiMessages.EMAIL_SUBJECT_LOGIN.getMessage(), emailText)
                .thenApplyAsync(result -> true)
                .exceptionally(ex -> false);
		log.warn("<<< Exiting sendLoginEmail(user={},loginTime={},loginLocation={}) -> {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), loginTime, loginLocation, logriteResult);
        return logriteResult;
    }

}
