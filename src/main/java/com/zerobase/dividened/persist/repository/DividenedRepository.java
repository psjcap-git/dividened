package com.zerobase.dividened.persist.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zerobase.dividened.persist.entity.DividenedEntity;

public interface DividenedRepository extends JpaRepository<DividenedEntity, Long> {
    List<DividenedEntity> findAllByCompanyId(Long companyId);
    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);
    void deleteAllByCompanyId(Long companyId);
}
