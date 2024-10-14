package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardnetJobReadDataJPARepository extends JpaRepository<CardnetJob, UUID>,
        JpaSpecificationExecutor<CardnetJob> {
    Optional<CardnetJob> findByTransactionId(UUID uuid);
    Optional<CardnetJob> findBySession(String session);


}
