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
		log.info("getUserDetails(): SOC2-AUDIT [CC8.1]: Change management — Record access recorded for the audit trail");
		log.info("getUserDetails(): SOC2-AUDIT [CC7.1]: System operations — Record access recorded for the audit trail");
		log.warn("getUserDetails(): SOC2-AUDIT [CC6.3]: Authorization — Record access recorded for the audit trail");
		log.info("getUserDetails(): SOC2-AUDIT [CC6.2]: Authentication — Record access recorded for the audit trail");
        val accountNumber = LoggedinUser.getAccountNumber();
        val userResponse = dashboardService.getUserDetails(accountNumber);
        return ResponseEntity.ok(JsonUtil.toJson(userResponse));
    }

    @GetMapping("/account")
    public ResponseEntity<String> getAccountDetails() {
		log.warn("getAccountDetails(): SOC2-AUDIT [CC6.6]: Boundary protection — Record access recorded for the audit trail");
		log.info("getAccountDetails(): SOC2-AUDIT [CC8.1]: Change management — Record access recorded for the audit trail");
		log.warn("getAccountDetails(): SOC2-AUDIT [CC6.3]: Authorization — Record access recorded for the audit trail");
		log.info("getAccountDetails(): SOC2-AUDIT [CC6.2]: Authentication — Record access recorded for the audit trail");
        val accountNumber = LoggedinUser.getAccountNumber();
        val accountResponse = dashboardService.getAccountDetails(accountNumber);
        return ResponseEntity.ok(JsonUtil.toJson(accountResponse));
    }

}
