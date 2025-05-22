package com.example.demo.repository;

import com.example.demo.domain.dto.fairyTale.PageResponse;
import com.example.demo.entity.base.FairyTale;
import com.example.demo.entity.base.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {

    List<Page> findByFairyTaleOrderByIdAsc(FairyTale fairyTale);
    List<Page> findAllByFairyTale(FairyTale fairyTale);
    Optional<Page> findTopByFairyTaleOrderByIdDesc(FairyTale fairyTale);

}