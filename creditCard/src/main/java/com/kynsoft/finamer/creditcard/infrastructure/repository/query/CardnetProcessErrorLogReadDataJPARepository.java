package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetProcessErrorLogDto;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetProcessErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardnetProcessErrorLogReadDataJPARepository extends JpaRepository<CardnetProcessErrorLog, UUID> {

    Boolean existsByTransactionId(UUID id);
}
