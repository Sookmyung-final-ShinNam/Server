package com.example.demo.repository;

import com.example.demo.entity.base.User;
import com.example.demo.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 1. username을 기준으로 사용자를 조회
    Optional<User> findByUsername(String username);

    // 2. username과 활성화 상태(active)를 기준으로 사용자를 조회
    Optional<User> findByUsernameAndActive(String username, Status active);

    // 3. 상태가 WITHDRAWN이면서 withdrawTime이 현재 시간보다 일정 이상 경과한 사용자들을 조회
    List<User> findByActiveAndWithdrawAtBefore(Status active, LocalDateTime time);

}