package com.example.demo.repository;

import com.example.demo.entity.base.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(String userId);
    Optional<Payment> findByTid(String tid);

}