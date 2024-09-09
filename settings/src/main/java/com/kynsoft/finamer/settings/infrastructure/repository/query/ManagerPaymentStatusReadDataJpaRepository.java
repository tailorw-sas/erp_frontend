package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManagerPaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ManagerPaymentStatusReadDataJpaRepository extends JpaRepository<ManagerPaymentStatus, UUID> {
    Page<ManagerPaymentStatus> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManagerPaymentStatus b WHERE b.code = :code AND b.id <> :id")
    Long countByCode(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagerPaymentStatus b WHERE b.applied = true AND b.id <> :id")
    Long countByAppliedAndNotId(@Param("id") UUID id);
}
