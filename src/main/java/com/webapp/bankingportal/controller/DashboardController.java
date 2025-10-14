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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/user")
    public ResponseEntity<String> getUserDetails() {
        val accountNumber = LoggedinUser.getAccountNumber();
		log.debug(">>> Entering getUserDetails()");
		log.debug("getUserDetails(): accountNumber → {}", accountNumber);
        val userResponse = dashboardService.getUserDetails(accountNumber);
		log.debug("getUserDetails(): userResponse → {}", userResponse);
		log.debug("<<< Exiting getUserDetails()");
        return ResponseEntity.ok(JsonUtil.toJson(userResponse));
    }

    @GetMapping("/account")
    public ResponseEntity<String> getAccountDetails() {
        val accountNumber = LoggedinUser.getAccountNumber();
		log.debug(">>> Entering getAccountDetails()");
		log.debug("getAccountDetails(): accountNumber → {}", accountNumber);
        val accountResponse = dashboardService.getAccountDetails(accountNumber);
		log.debug("getAccountDetails(): accountResponse → {}", accountResponse);
		log.debug("<<< Exiting getAccountDetails()");
        return ResponseEntity.ok(JsonUtil.toJson(accountResponse));
    }

}
