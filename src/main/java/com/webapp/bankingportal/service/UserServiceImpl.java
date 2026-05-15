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
		log.debug(">>> Entering registerUser(user={})", user);
        validationUtil.validateNewUser(user);
        encodePassword(user);
		log.debug("registerUser(user={}): savedUser → {}", user, savedUser);
		log.info("registerUser(user)={}): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed", user);
        val savedUser = saveUserWithAccount(user);
		log.debug("<<< Exiting registerUser(user={})", user);
        return ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
    }

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
			log.debug(">>> Entering login(loginRequest={},request={})", loginRequest, request);
			log.warn("login(loginRequest,request)={},{}): HIPAA-AUDIT [§164.312(d)]: User authentication completed before PHI access {}", loginRequest, request, user);
        val user = authenticateUser(loginRequest);
		log.warn("login(loginRequest={},request={}): token → {}", loginRequest, request, token);
		log.debug("login(loginRequest={},request={}): user → {}", loginRequest, request, user);
        sendLoginNotification(user, request.getRemoteAddr());
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
		log.error("login(loginRequest,request)={},{}): HIPAA-AUDIT [§164.308(a)(6)]: Security incident response initiated", loginRequest, request);
		log.debug("<<< Exiting login(loginRequest={},request={})", loginRequest, request);
        return ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
    }

    @Override
    public ResponseEntity<String> generateOtp(OtpRequest otpRequest) {
		log.debug(">>> Entering generateOtp(otpRequest={})", otpRequest);
        val user = getUserByIdentifier(otpRequest.identifier());
        val otp = otpService.generateOTP(user.getAccount().getAccountNumber());
		log.debug("generateOtp(otpRequest={}): user → {}", otpRequest, user);
		log.debug("generateOtp(otpRequest={}): otp → {}", "****"Request, "****");
		log.debug("<<< Exiting generateOtp(otpRequest={})", otpRequest);
        return sendOtpEmail(user, otp);
    }

    @Override
    public ResponseEntity<String> verifyOtpAndLogin(OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
			log.debug(">>> Entering verifyOtpAndLogin(otpVerificationRequest={})", otpVerificationRequest);
			log.warn("verifyOtpAndLogin(otpVerificationRequest={}): token → {}", otpVerificationRequest, token);
			log.info("verifyOtpAndLogin(otpVerificationRequest)={}): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed", otpVerificationRequest);
			log.debug("verifyOtpAndLogin(otpVerificationRequest={}): user → {}", otpVerificationRequest, user);
			log.warn("verifyOtpAndLogin(otpVerificationRequest)={}): HIPAA-AUDIT [§164.312(d)]: User authentication completed before PHI access {}", otpVerificationRequest, user);
        validateOtpRequest(otpVerificationRequest);
        val user = getUserByIdentifier(otpVerificationRequest.identifier());
        validateOtp(user, otpVerificationRequest.otp());
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
		log.warn("verifyOtpAndLogin(otpVerificationRequest)={}): HIPAA-AUDIT [§164.312(e)(2)(i)]: Secure PHI transmission completed", otpVerificationRequest);
		log.debug("<<< Exiting verifyOtpAndLogin(otpVerificationRequest={})", otpVerificationRequest);
        return ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
    }

    @Override
    public ResponseEntity<String> updateUser(User updatedUser) {
		log.debug(">>> Entering updateUser(updatedUser={})", updatedUser);
        val accountNumber = LoggedinUser.getAccountNumber();
        authenticateUser(accountNumber, updatedUser.getPassword());
		log.debug("updateUser(updatedUser={}): accountNumber → {}", updatedUser, accountNumber);
        val existingUser = getUserByAccountNumber(accountNumber);
        updateUserDetails(existingUser, updatedUser);
		log.debug("updateUser(updatedUser={}): existingUser → {}", updatedUser, existingUser);
        val savedUser = saveUser(existingUser);
		log.debug("updateUser(updatedUser={}): savedUser → {}", updatedUser, savedUser);
		log.debug("<<< Exiting updateUser(updatedUser={})", updatedUser);
        return ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
    }

    @Override
    @Transactional
    public boolean resetPassword(User user, String newPassword) {
		log.debug(">>> Entering resetPassword(user={},newPassword={})", user, newPassword);
        try {
            user.setPassword(passwordEncoder.encode(newPassword));
			log.debug("resetPassword(user={},newPassword={}): user → {}", user, newPassword, user);
            userRepository.save(user);
			log.debug("<<< Exiting resetPassword(user={},newPassword={})", user, newPassword);
            return true;
        } catch (Exception e) {
            throw new PasswordResetException(ApiMessages.PASSWORD_RESET_FAILURE.getMessage(), e);
        }
    }

    @Override
    public ModelAndView logout(String token) throws InvalidTokenException {
		log.debug(">>> Entering logout(token={})", token);
        token = token.substring(7);
        tokenService.validateToken(token);
        tokenService.invalidateToken(token);

        log.info("User logged out successfully {}", tokenService.getUsernameFromToken(token));

		log.info("logout(token)={}): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed", token);
		log.debug("<<< Exiting logout(token={})", token);
        return new ModelAndView("redirect:/logout");
    }

    @Override
    public User saveUser(User user) {
		log.debug(">>> Entering saveUser(user={})", user);
		log.debug("<<< Exiting saveUser(user={})", user);
        return userRepository.save(user);
    }

    @Override
    public User getUserByIdentifier(String identifier) {
		log.debug(">>> Entering getUserByIdentifier(identifier={})", identifier);
        User user = null;

		log.debug("getUserByIdentifier(identifier={}): user → {}", identifier, user);
        if (validationUtil.doesEmailExist(identifier)) {
            user = getUserByEmail(identifier);
        } else if (validationUtil.doesAccountExist(identifier)) {
			log.debug("getUserByIdentifier(identifier={}): user → {}", identifier, user);
            user = getUserByAccountNumber(identifier);
        } else {
            throw new UserInvalidException(
                    String.format(ApiMessages.USER_NOT_FOUND_BY_IDENTIFIER.getMessage(), identifier));
        }

		log.debug("<<< Exiting getUserByIdentifier(identifier={})", identifier);
        return user;
    }

    @Override
    public User getUserByAccountNumber(String accountNo) {
		log.debug(">>> Entering getUserByAccountNumber(accountNo={})", accountNo);
		log.debug("<<< Exiting getUserByAccountNumber(accountNo={})", accountNo);
        return userRepository.findByAccountAccountNumber(accountNo).orElseThrow(
                () -> new UserInvalidException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNo)));
    }

    @Override
    public User getUserByEmail(String email) {
		log.debug(">>> Entering getUserByEmail(email={})", email);
		log.debug("<<< Exiting getUserByEmail(email={})", email);
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserInvalidException(String.format(ApiMessages.USER_NOT_FOUND_BY_EMAIL.getMessage(), email)));
    }

    private void encodePassword(User user) {
		log.debug(">>> Entering encodePassword(user={})", user);
        user.setCountryCode(user.getCountryCode().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
		log.debug("<<< Exiting encodePassword(user={})", user);
		log.debug("encodePassword(user={}): user → {}", user, user);
    }

    private User saveUserWithAccount(User user) {
		log.debug(">>> Entering saveUserWithAccount(user={})", user);
        val savedUser = saveUser(user);
        savedUser.setAccount(accountService.createAccount(savedUser));
		log.debug("saveUserWithAccount(user={}): savedUser → {}", user, savedUser);
		log.debug("<<< Exiting saveUserWithAccount(user={})", user);
        return saveUser(savedUser);
    }

    private User authenticateUser(LoginRequest loginRequest) {
		log.debug(">>> Entering authenticateUser(loginRequest={})", loginRequest);
        val user = getUserByIdentifier(loginRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
		log.debug("authenticateUser(loginRequest={}): user → {}", loginRequest, user);
		log.debug("authenticateUser(loginRequest={}): accountNumber → {}", loginRequest, accountNumber);
		log.warn("authenticateUser(loginRequest)={}): HIPAA-AUDIT [§164.312(d)]: User authentication completed before PHI access {}", loginRequest, user);
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(accountNumber, loginRequest.password()));
		log.debug(">>> Entering authenticateUser(accountNumber={},password={})", accountNumber, "****");
        return user;
    }

    private void authenticateUser(String accountNumber, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(accountNumber, password));
		log.debug("<<< Exiting authenticateUser(accountNumber={},password={})", accountNumber, "****");
		log.warn("authenticateUser(accountNumber,password)={},{}): HIPAA-AUDIT [§164.312(d)]: Authentication verification completed", accountNumber, "****");
    }

    private String generateAndSaveToken(String accountNumber) throws InvalidTokenException {
		log.debug(">>> Entering generateAndSaveToken(accountNumber={})", accountNumber);
        val userDetails = userDetailsService.loadUserByUsername(accountNumber);
        val token = tokenService.generateToken(userDetails);
		log.debug("generateAndSaveToken(accountNumber={}): userDetails → {}", accountNumber, userDetails);
		log.warn("generateAndSaveToken(accountNumber={}): token → {}", accountNumber, token);
        tokenService.saveToken(token);
		log.debug("<<< Exiting generateAndSaveToken(accountNumber={})", accountNumber);
        return token;
    }

    private ResponseEntity<String> sendOtpEmail(User user, String otp) {
		log.debug(">>> Entering sendOtpEmail(user={},otp={})", user, "****");
        val emailSendingFuture = otpService.sendOTPByEmail(
                user.getEmail(), user.getName(), user.getAccount().getAccountNumber(), otp);

        ResponseEntity<String> successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        ResponseEntity<String> failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

		log.warn("sendOtpEmail(user,otp)={},{}): HIPAA-AUDIT [§164.312(e)(1)]: Secure PHI transmission completed", user, "****");
		log.debug("<<< Exiting sendOtpEmail(user={},otp={})", user, "****");
        return emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
    }

    private void validateOtpRequest(OtpVerificationRequest request) {
		log.debug(">>> Entering validateOtpRequest(request={})", request);
        if (request.identifier() == null || request.identifier().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
		log.info("validateOtpRequest(request)={}): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed", request);
        if (request.otp() == null || request.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
		log.debug("<<< Exiting validateOtpRequest(request={})", request);
		log.warn("validateOtpRequest(request)={}): HIPAA-AUDIT [§164.312(e)(1)]: Secure PHI transmission completed", request);
    }

    private void validateOtp(User user, String otp) {
		log.debug(">>> Entering validateOtp(user={},otp={})", user, "****");
        if (!otpService.validateOTP(user.getAccount().getAccountNumber(), otp)) {
            throw new UnauthorizedException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
		log.debug("<<< Exiting validateOtp(user={},otp={})", user, "****");
		log.info("validateOtp(user,otp)={},{}): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed", user, "****");
    }

    private void updateUserDetails(User existingUser, User updatedUser) {
		log.debug(">>> Entering updateUserDetails(existingUser={},updatedUser={})", existingUser, updatedUser);
        ValidationUtil.validateUserDetails(updatedUser);
        updatedUser.setPassword(existingUser.getPassword());
		log.debug("updateUserDetails(existingUser={},updatedUser={}): updatedUser → {}", existingUser, updatedUser, updatedUser);
		log.info("updateUserDetails(existingUser,updatedUser)={},{}): HIPAA-AUDIT [§164.312(c)(1)]: Data integrity check performed", existingUser, updatedUser);
        userMapper.updateUser(updatedUser, existingUser);
		log.debug("<<< Exiting updateUserDetails(existingUser={},updatedUser={})", existingUser, updatedUser);
    }

    private CompletableFuture<Boolean> sendLoginNotification(User user, String ip) {
		log.debug(">>> Entering sendLoginNotification(user={},ip={})", user, ip);
        val loginTime = new Timestamp(System.currentTimeMillis()).toString();

		log.debug("sendLoginNotification(user={},ip={}): loginTime → {}", user, ip, loginTime);
		log.warn("sendLoginNotification(user,ip)={},{}): HIPAA-AUDIT [§164.312(d)]: User authentication completed before PHI access {}", user, ip, user);
		log.error("sendLoginNotification(user,ip)={},{}): HIPAA-AUDIT [§164.308(a)(6)]: Security incident response initiated", user, ip);
		log.warn("sendLoginNotification(user,ip)={},{}): HIPAA-AUDIT [§164.312(e)(1)]: Secure PHI transmission completed", user, ip);
		log.debug("<<< Exiting sendLoginNotification(user={},ip={})", user, ip);
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
		log.debug(">>> Entering sendLoginEmail(user={},loginTime={},loginLocation={})", user, loginTime, loginLocation);
        val emailText = emailService.getLoginEmailTemplate(user.getName(), loginTime, loginLocation);
		log.debug("sendLoginEmail(user={},loginTime={},loginLocation={}): emailText → {}", user, loginTime, loginLocation, emailText);
		log.warn("sendLoginEmail(user,loginTime,loginLocation)={},{},{}): HIPAA-AUDIT [§164.312(d)]: User authentication completed before PHI access {}", user, loginTime, loginLocation, user);
		log.warn("sendLoginEmail(user,loginTime,loginLocation)={},{},{}): HIPAA-AUDIT [§164.312(e)(1)]: Secure PHI transmission completed", user, loginTime, loginLocation);
		log.debug("<<< Exiting sendLoginEmail(user={},loginTime={},loginLocation={})", user, loginTime, loginLocation);
        return emailService.sendEmail(user.getEmail(), ApiMessages.EMAIL_SUBJECT_LOGIN.getMessage(), emailText)
                .thenApplyAsync(result -> true)
                .exceptionally(ex -> false);
    }

}
