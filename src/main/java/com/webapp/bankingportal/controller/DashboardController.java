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
public static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    @GetMapping("/user")
    public ResponseEntity<String> getUserDetails() {
		log.info(">>> Entering getUserDetails()");
        val accountNumber = LoggedinUser.getAccountNumber();
        val userResponse = dashboardService.getUserDetails(accountNumber);
		log.info("<<< Exiting getUserDetails()");
        return ResponseEntity.ok(JsonUtil.toJson(userResponse));
    }

    @GetMapping("/account")
    public ResponseEntity<String> getAccountDetails() {
		log.info(">>> Entering getAccountDetails()");
        val accountNumber = LoggedinUser.getAccountNumber();
        val accountResponse = dashboardService.getAccountDetails(accountNumber);
		log.info("<<< Exiting getAccountDetails()");
        return ResponseEntity.ok(JsonUtil.toJson(accountResponse));
    }

}
