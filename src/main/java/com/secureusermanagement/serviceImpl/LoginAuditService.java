package com.secureusermanagement.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.secureusermanagement.entity.LoginAuditLog;
import com.secureusermanagement.repository.LoginAuditLogRepository;

@Service
public class LoginAuditService {

    @Autowired
    private LoginAuditLogRepository loginAuditLogRepository;

    public void logLogin(String email, String ipAddress, String userAgent, String status, String reason) {
        LoginAuditLog log = new LoginAuditLog();
        log.setEmail(email);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setLoginStatus(status);
        log.setFailureReason(reason);
        log.setLoginTime(LocalDateTime.now());

        loginAuditLogRepository.save(log);
    }
}