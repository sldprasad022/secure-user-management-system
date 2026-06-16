package com.secureusermanagement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
public class LoginAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String ipAddress;
    private String userAgent;
    private String loginStatus; // SUCCESS or FAILED
    private String failureReason; // For failed attempts
    private LocalDateTime loginTime;
}