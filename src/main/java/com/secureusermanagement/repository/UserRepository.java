package com.secureusermanagement.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.secureusermanagement.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);
	
	boolean existsByMobileNumber(String mobileNumber);
	
	void deleteByIsEmailVerifiedFalseAndCreatedAtBefore(LocalDateTime cutoff);
	
	@Transactional
	@Modifying
	@Query("""
	       DELETE FROM User u
	       WHERE u.isEmailVerified = false
	       AND u.createdAt < :cutoff
	       """)
	void deleteUnverifiedUsers(@Param("cutoff") LocalDateTime cutoff);
	
	Optional<User> findByEmailOrMobileNumber(String email, String mobileNumber);
	
	long countByIsActiveTrue();
	
	long countByIsActiveFalse();
	
    Page<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String userName,String email,Pageable pageable);
	
	Page<User> findByIsActive(boolean isActive,Pageable pageable);
}
