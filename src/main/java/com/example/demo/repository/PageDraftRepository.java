package com.example.demo.repository;

import com.example.demo.entity.base.FairyTale;
import com.example.demo.entity.base.PageDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageDraftRepository extends JpaRepository<PageDraft, Long> {

    List<PageDraft> findByFairyTaleOrderByIdAsc(FairyTale fairyTale);

    Optional<PageDraft> findTopByFairyTaleOrderByIdDesc(FairyTale fairyTale);

    void deleteByFairyTale(FairyTale fairyTale);

    void deleteByFairyTaleIdAndFairyTaleUserId(Long fairyTaleId, Long userId);
}