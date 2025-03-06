package com.example.demo.repository;

import com.example.demo.entity.base.User;
import com.example.demo.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);
    Optional<User> findByUserName(String userName);
    Optional<User> findByUserIdAndActive(String userId, Status active);
    List<User> findByActiveAndWithdrawTimeBefore(Status active, LocalDateTime time);

}