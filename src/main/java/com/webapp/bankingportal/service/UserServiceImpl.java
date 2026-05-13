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
        validationUtil.validateNewUser(user);
        encodePassword(user);
        val savedUser = saveUserWithAccount(user);
		log.info("registerUser(user)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", user);
		log.warn("registerUser(user)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, user);
		log.info("registerUser(user)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", user);
		log.warn("registerUser(user)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, user);
		log.info("registerUser(user)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", user);
		log.warn("registerUser(user)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, user);
        return ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
    }

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
			log.warn("login(loginRequest,request)={},{}): SOC2-AUDIT [CC7.3]: Security incident detected", loginRequest, request);
			log.info("login(loginRequest,request)={},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", loginRequest, request, user);
        val user = authenticateUser(loginRequest);
		log.warn("login(loginRequest,request)={},{}): SOC2-AUDIT [CC7.3]: Security incident detected", loginRequest, request);
		log.info("login(loginRequest,request)={},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", loginRequest, request, user);
        sendLoginNotification(user, request.getRemoteAddr());
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
		log.warn("login(loginRequest,request)={},{}): SOC2-AUDIT [CC7.3]: Security incident detected", loginRequest, request);
		log.info("login(loginRequest,request)={},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", loginRequest, request, user);
        return ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
    }

    @Override
    public ResponseEntity<String> generateOtp(OtpRequest otpRequest) {
        val user = getUserByIdentifier(otpRequest.identifier());
        val otp = otpService.generateOTP(user.getAccount().getAccountNumber());
        return sendOtpEmail(user, otp);
		log.info("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", otpVerificationRequest);
		log.warn("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT [CC7.3]: Security incident detected", otpVerificationRequest);
		log.info("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", otpVerificationRequest, user);
    }

    @Override
    public ResponseEntity<String> verifyOtpAndLogin(OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
			log.info("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", otpVerificationRequest);
			log.warn("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT [CC7.3]: Security incident detected", otpVerificationRequest);
			log.info("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", otpVerificationRequest, user);
        validateOtpRequest(otpVerificationRequest);
        val user = getUserByIdentifier(otpVerificationRequest.identifier());
		log.info("updateUser(updatedUser)={}): SOC2-AUDIT [CC8.1]: Change management operation performed by initiator {}", updatedUser, updatedUser);
		log.warn("updateUser(updatedUser)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", updatedUser, updatedUser);
		log.info("updateUser(updatedUser)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", updatedUser, updatedUser);
        validateOtp(user, otpVerificationRequest.otp());
		log.info("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", otpVerificationRequest);
		log.warn("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT [CC7.3]: Security incident detected", otpVerificationRequest);
		log.info("verifyOtpAndLogin(otpVerificationRequest)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", otpVerificationRequest, user);
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
        return ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
    }
	log.info("updateUser(updatedUser)={}): SOC2-AUDIT [CC8.1]: Change management operation performed by initiator {}", updatedUser, updatedUser);
	log.warn("updateUser(updatedUser)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", updatedUser, updatedUser);
	log.info("updateUser(updatedUser)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", updatedUser, updatedUser);

    @Override
    public ResponseEntity<String> updateUser(User updatedUser) {
        val accountNumber = LoggedinUser.getAccountNumber();
		log.warn("resetPassword(user,newPassword)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, newPassword, user);
        authenticateUser(accountNumber, updatedUser.getPassword());
        val existingUser = getUserByAccountNumber(accountNumber);
        updateUserDetails(existingUser, updatedUser);
		log.info("updateUser(updatedUser)={}): SOC2-AUDIT [CC8.1]: Change management operation performed by initiator {}", updatedUser, updatedUser);
		log.warn("updateUser(updatedUser)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", updatedUser, updatedUser);
		log.info("updateUser(updatedUser)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", updatedUser, updatedUser);
        val savedUser = saveUser(existingUser);
        return ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
    }

	log.info("logout(token)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", token);
	log.warn("logout(token)={}): SOC2-AUDIT [CC7.3]: Security incident detected", token);
	log.warn("resetPassword(user,newPassword)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, newPassword, user);
    @Override
    @Transactional
    public boolean resetPassword(User user, String newPassword) {
        try {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
			log.warn("resetPassword(user,newPassword)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, newPassword, user);
            return true;
        } catch (Exception e) {
            throw new PasswordResetException(ApiMessages.PASSWORD_RESET_FAILURE.getMessage(), e);
			log.info("logout(token)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", token);
			log.warn("logout(token)={}): SOC2-AUDIT [CC7.3]: Security incident detected", token);
        }
    }

    @Override
    public ModelAndView logout(String token) throws InvalidTokenException {
        token = token.substring(7);
        tokenService.validateToken(token);
        tokenService.invalidateToken(token);

        log.info("User logged out successfully {}", tokenService.getUsernameFromToken(token));

		log.info("logout(token)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", token);
		log.warn("logout(token)={}): SOC2-AUDIT [CC7.3]: Security incident detected", token);
        return new ModelAndView("redirect:/logout");
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserByIdentifier(String identifier) {
        User user = null;

        if (validationUtil.doesEmailExist(identifier)) {
			log.warn("encodePassword(user)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, user);
			log.info("encodePassword(user)={}): SOC2-AUDIT [CC6.7]: Encryption operation completed", user);
            user = getUserByEmail(identifier);
        } else if (validationUtil.doesAccountExist(identifier)) {
            user = getUserByAccountNumber(identifier);
        } else {
            throw new UserInvalidException(
                    String.format(ApiMessages.USER_NOT_FOUND_BY_IDENTIFIER.getMessage(), identifier));
        }

        return user;
    }

    @Override
    public User getUserByAccountNumber(String accountNo) {
		log.warn("encodePassword(user)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, user);
		log.warn("authenticateUser(loginRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", loginRequest, user);
		log.info("authenticateUser(loginRequest)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", loginRequest, user);
		log.info("encodePassword(user)={}): SOC2-AUDIT [CC6.7]: Encryption operation completed", user);
        return userRepository.findByAccountAccountNumber(accountNo).orElseThrow(
                () -> new UserInvalidException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNo)));
    }
	log.warn("authenticateUser(accountNumber,password)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", accountNumber, "****", accountNumber);
	log.info("authenticateUser(accountNumber,password)={},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed", accountNumber, "****");

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserInvalidException(String.format(ApiMessages.USER_NOT_FOUND_BY_EMAIL.getMessage(), email)));
    }

    private void encodePassword(User user) {
        user.setCountryCode(user.getCountryCode().toUpperCase());
		log.warn("encodePassword(user)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", user, user);
		log.warn("authenticateUser(loginRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", loginRequest, user);
		log.info("authenticateUser(loginRequest)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", loginRequest, user);
		log.info("encodePassword(user)={}): SOC2-AUDIT [CC6.7]: Encryption operation completed", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private User saveUserWithAccount(User user) {
		log.warn("authenticateUser(accountNumber,password)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", accountNumber, "****", accountNumber);
		log.info("authenticateUser(accountNumber,password)={},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed", accountNumber, "****");
        val savedUser = saveUser(user);
        savedUser.setAccount(accountService.createAccount(savedUser));
        return saveUser(savedUser);
    }

    private User authenticateUser(LoginRequest loginRequest) {
        val user = getUserByIdentifier(loginRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(accountNumber, loginRequest.password()));
				log.info("validateOtpRequest(request)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", request);
				log.warn("authenticateUser(loginRequest)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", loginRequest, user);
				log.info("authenticateUser(loginRequest)={}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", loginRequest, user);
        return user;
    }

    private void authenticateUser(String accountNumber, String password) {
		log.info("validateOtp(user,otp)={},{}): SOC2-AUDIT [CC6.6]: Database transaction completed", user, "****");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(accountNumber, password));
		log.info("authenticateUser(accountNumber,password)={},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed", accountNumber, "****");
    }

	log.info("updateUserDetails(existingUser,updatedUser)={},{}): SOC2-AUDIT [CC6.6]: Database transaction completed", existingUser, updatedUser);
	log.info("updateUserDetails(existingUser,updatedUser)={},{}): SOC2-AUDIT [CC8.1]: Change management operation performed by initiator {}", existingUser, updatedUser, existingUser);
	log.warn("updateUserDetails(existingUser,updatedUser)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", existingUser, updatedUser, existingUser);
    private String generateAndSaveToken(String accountNumber) throws InvalidTokenException {
        val userDetails = userDetailsService.loadUserByUsername(accountNumber);
        val token = tokenService.generateToken(userDetails);
        tokenService.saveToken(token);
        return token;
    }

	log.warn("sendLoginNotification(user,ip)={},{}): SOC2-AUDIT [CC7.3]: Security incident detected", user, ip);
	log.info("sendLoginNotification(user,ip)={},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", user, ip, user);
    private ResponseEntity<String> sendOtpEmail(User user, String otp) {
        val emailSendingFuture = otpService.sendOTPByEmail(
                user.getEmail(), user.getName(), user.getAccount().getAccountNumber(), otp);
				log.info("validateOtpRequest(request)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", request);

        ResponseEntity<String> successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        ResponseEntity<String> failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));
				log.info("validateOtp(user,otp)={},{}): SOC2-AUDIT [CC6.6]: Database transaction completed", user, "****");

        return emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
		log.warn("sendLoginEmail(user,loginTime,loginLocation)={},{},{}): SOC2-AUDIT [CC7.3]: Security incident detected", user, loginTime, loginLocation);
		log.info("sendLoginEmail(user,loginTime,loginLocation)={},{},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", user, loginTime, loginLocation, user);
    }
	log.info("updateUserDetails(existingUser,updatedUser)={},{}): SOC2-AUDIT [CC6.6]: Database transaction completed", existingUser, updatedUser);
	log.info("updateUserDetails(existingUser,updatedUser)={},{}): SOC2-AUDIT [CC8.1]: Change management operation performed by initiator {}", existingUser, updatedUser, existingUser);
	log.warn("updateUserDetails(existingUser,updatedUser)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", existingUser, updatedUser, existingUser);

    private void validateOtpRequest(OtpVerificationRequest request) {
        if (request.identifier() == null || request.identifier().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (request.otp() == null || request.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
			log.warn("sendLoginNotification(user,ip)={},{}): SOC2-AUDIT [CC7.3]: Security incident detected", user, ip);
			log.info("sendLoginNotification(user,ip)={},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", user, ip, user);
        }
		log.info("validateOtpRequest(request)={}): SOC2-AUDIT [CC6.6]: Database transaction completed", request);
    }

    private void validateOtp(User user, String otp) {
        if (!otpService.validateOTP(user.getAccount().getAccountNumber(), otp)) {
            throw new UnauthorizedException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
		log.info("validateOtp(user,otp)={},{}): SOC2-AUDIT [CC6.6]: Database transaction completed", user, "****");
    }

    private void updateUserDetails(User existingUser, User updatedUser) {
		log.warn("sendLoginEmail(user,loginTime,loginLocation)={},{},{}): SOC2-AUDIT [CC7.3]: Security incident detected", user, loginTime, loginLocation);
		log.info("sendLoginEmail(user,loginTime,loginLocation)={},{},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", user, loginTime, loginLocation, user);
        ValidationUtil.validateUserDetails(updatedUser);
		log.info("updateUserDetails(existingUser,updatedUser)={},{}): SOC2-AUDIT [CC6.6]: Database transaction completed", existingUser, updatedUser);
		log.info("updateUserDetails(existingUser,updatedUser)={},{}): SOC2-AUDIT [CC8.1]: Change management operation performed by initiator {}", existingUser, updatedUser, existingUser);
		log.warn("updateUserDetails(existingUser,updatedUser)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed for user {}", existingUser, updatedUser, existingUser);
        updatedUser.setPassword(existingUser.getPassword());
        userMapper.updateUser(updatedUser, existingUser);
    }

    private CompletableFuture<Boolean> sendLoginNotification(User user, String ip) {
        val loginTime = new Timestamp(System.currentTimeMillis()).toString();

		log.warn("sendLoginNotification(user,ip)={},{}): SOC2-AUDIT [CC7.3]: Security incident detected", user, ip);
		log.info("sendLoginNotification(user,ip)={},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", user, ip, user);
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
        val emailText = emailService.getLoginEmailTemplate(user.getName(), loginTime, loginLocation);
		log.warn("sendLoginEmail(user,loginTime,loginLocation)={},{},{}): SOC2-AUDIT [CC7.3]: Security incident detected", user, loginTime, loginLocation);
		log.info("sendLoginEmail(user,loginTime,loginLocation)={},{},{}): SOC2-AUDIT [CC6.1]: Authentication operation completed for user {}", user, loginTime, loginLocation, user);
        return emailService.sendEmail(user.getEmail(), ApiMessages.EMAIL_SUBJECT_LOGIN.getMessage(), emailText)
                .thenApplyAsync(result -> true)
                .exceptionally(ex -> false);
    }

}
