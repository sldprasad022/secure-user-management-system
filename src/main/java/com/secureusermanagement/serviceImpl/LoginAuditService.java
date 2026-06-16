package com.secureusermanagement.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.secureusermanagement.entity.LoginAuditLog;
import com.secureusermanagement.enums.LoginStatus;
import com.secureusermanagement.repository.LoginAuditLogRepository;

@Service
public class LoginAuditService
{
    @Autowired
    private LoginAuditLogRepository loginAuditLogRepository;

    public void logLogin(Long userId,
                         String email,
                         String ipAddress,
                         String userAgent,
                         LoginStatus status,
                         String reason)
    {
        LoginAuditLog log = new LoginAuditLog();

        log.setUserId(userId);
        log.setEmail(email);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setLoginStatus(status);
        log.setFailureReason(reason);
        log.setLoginTime(LocalDateTime.now());

        loginAuditLogRepository.save(log);
    }
}