package com.example.demo.service.general;

import com.example.demo.entity.base.User;
import com.example.demo.entity.enums.Status;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserDeleteService {

    private final UserRepository userRepository;
    private final long deleteTimeMillis;

    public UserDeleteService(UserRepository userRepository,
                             @Value("${withdrawn.delete-time}") String deleteTime) {
        this.userRepository = userRepository;
        this.deleteTimeMillis = Long.parseLong(deleteTime) * 1000L;
    }

    @Scheduled(fixedRateString = "#{${withdrawn.check-time} * 1000}")
    @Transactional
    public void deleteWithdrawnUsers() {
        boolean isDeleted;
        do {
            LocalDateTime now = LocalDateTime.now();
            List<User> withdrawnUsers = userRepository.findByActiveAndWithdrawTimeBefore(
                    Status.WITHDRAWN,
                    now.minusSeconds(deleteTimeMillis / 1000)
            );
            isDeleted = !withdrawnUsers.isEmpty();
            for (User user : withdrawnUsers) {
                userRepository.delete(user);
            }
        } while (isDeleted);
    }

}