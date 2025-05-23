package com.example.demo.domain.repository;

import com.example.demo.domain.entity.Fairy;
import com.example.demo.domain.entity.FairyParticipation;
import com.example.demo.domain.entity.FairyTale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FairyParticipationRepository extends JpaRepository<FairyParticipation, Long> {

    // 첫 번째 요정만 필요할 경우 (탄생 동화이므로 출연 요정이 1명임)
    Optional<FairyParticipation> findFirstByFairyTale(FairyTale fairyTale);

    boolean existsByFairyAndFairyTale(Fairy fairy, FairyTale fairyTale);
}
