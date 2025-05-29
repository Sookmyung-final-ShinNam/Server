package com.example.demo.domain.repository;

import com.example.demo.domain.entity.FairyTale;
import com.example.demo.domain.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {
    List<Page> findByFairyTale(FairyTale fairyTale);
}