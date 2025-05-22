package com.example.demo.login.service;

import com.example.demo.domain.repository.UserRepository;
import com.example.demo.domain.entity.enums.Status;
import com.example.demo.domain.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserDeleteService {

    private final UserRepository userRepository;
    private final long deleteTimeMillis; // 삭제 시간 (초 단위)

    public UserDeleteService(UserRepository userRepository,
                             @Value("${withdrawn.delete-time}") String deleteTime) {
        this.userRepository = userRepository;
        this.deleteTimeMillis = Long.parseLong(deleteTime) * 1000L;
    }

    // check-time 주기로 탈퇴 상태인 사용자 조회 및 삭제
    @Scheduled(fixedRateString = "#{${withdrawn.check-time} * 1000}")
    @Transactional
    public void deleteWithdrawnUsers() {
        boolean isDeleted;
        do {
            // 현재 시간
            LocalDateTime now = LocalDateTime.now();

            // deleteTimeMillis 이상 탈퇴 상태인 사용자 조회
            List<User> withdrawnUsers = userRepository.findByActiveAndWithdrawAtBefore(
                    Status.WITHDRAWN,
                    now.minusSeconds(deleteTimeMillis / 1000)
            );

            isDeleted = !withdrawnUsers.isEmpty(); // 삭제 여부 확인

            for (User user : withdrawnUsers) {
                // 사용자 삭제
                userRepository.delete(user);
            }
        } while (isDeleted); // 삭제된 사용자가 있으면 다시 수행 ( 대량의 사용자 삭제시에도 안정성을 보장하기 위함 )
    }
}