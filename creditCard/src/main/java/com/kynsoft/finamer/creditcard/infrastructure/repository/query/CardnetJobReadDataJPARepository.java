package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardnetJobReadDataJPARepository extends JpaRepository<CardnetJob, UUID>,
        JpaSpecificationExecutor<CardnetJob> {
    Optional<CardnetJob> findByTransactionId(UUID uuid);
    Optional<CardnetJob> findBySession(String session);
    @Query("SELECT b FROM CardnetJob b WHERE b.isProcessed = false and b.createdAt > :yesterdaydate")
    List<CardnetJob> findByIsProcessedFalse(@Param("yesterdaydate") LocalDateTime yesterdaydate);
}
