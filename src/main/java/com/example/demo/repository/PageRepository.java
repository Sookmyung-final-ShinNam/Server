package com.example.demo.repository;

import com.example.demo.entity.base.FairyTale;
import com.example.demo.entity.base.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {

    List<Page> findByFairyTaleOrderByIdAsc(FairyTale fairyTale);

}