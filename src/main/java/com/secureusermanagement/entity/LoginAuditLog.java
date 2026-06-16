package com.secureusermanagement.entity;

import java.time.LocalDateTime;

import com.secureusermanagement.enums.LoginStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAuditLog
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String email;

    private String ipAddress;

    private String userAgent;

    @Enumerated(EnumType.STRING)
    private LoginStatus loginStatus;

    private String failureReason;

    private LocalDateTime loginTime;
}