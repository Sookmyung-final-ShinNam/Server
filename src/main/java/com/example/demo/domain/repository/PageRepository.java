package com.example.demo.domain.repository;

import com.example.demo.domain.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {

}