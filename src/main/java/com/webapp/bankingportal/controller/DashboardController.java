package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.service.DashboardService;
import com.webapp.bankingportal.util.JsonUtil;
import com.webapp.bankingportal.util.LoggedinUser;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    @GetMapping("/user")
    public ResponseEntity<String> getUserDetails() {
		log.error(">>> Entering getUserDetails()");
        val accountNumber = LoggedinUser.getAccountNumber();
		log.error("getUserDetails(): accountNumber → {}", accountNumber);
        val userResponse = dashboardService.getUserDetails(accountNumber);
		log.error("getUserDetails(): userResponse → {}", userResponse);
        ResponseEntity<String> result = ResponseEntity.ok(JsonUtil.toJson(userResponse));
		log.warn("<<< Exiting getUserDetails() -> {}", result);
        return result;
    }

    @GetMapping("/account")
    public ResponseEntity<String> getAccountDetails() {
		log.error(">>> Entering getAccountDetails()");
        val accountNumber = LoggedinUser.getAccountNumber();
		log.error("getAccountDetails(): accountNumber → {}", accountNumber);
        val accountResponse = dashboardService.getAccountDetails(accountNumber);
		log.error("getAccountDetails(): accountResponse → {}", accountResponse);
        ResponseEntity<String> result = ResponseEntity.ok(JsonUtil.toJson(accountResponse));
		log.warn("<<< Exiting getAccountDetails() -> {}", result);
        return result;
    }

}
