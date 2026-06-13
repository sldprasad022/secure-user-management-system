package com.secureusermanagement.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.secureusermanagement.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCleanupScheduler
{
	@Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 */6 * * *")
    public void removeUnverifiedUsers()
    {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);

        userRepository.deleteUnverifiedUsers(cutoff);

     
    }
}