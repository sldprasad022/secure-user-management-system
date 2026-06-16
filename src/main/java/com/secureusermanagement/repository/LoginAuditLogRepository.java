package com.secureusermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.secureusermanagement.entity.LoginAuditLog;

@Repository
public interface LoginAuditLogRepository extends JpaRepository<LoginAuditLog, Long> {
}