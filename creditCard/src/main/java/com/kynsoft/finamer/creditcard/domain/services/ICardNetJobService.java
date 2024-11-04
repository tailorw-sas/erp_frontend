package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ICardNetJobService {
    UUID create(CardnetJobDto dto);
    CardnetJobDto findByTransactionId(UUID id);
    void update(CardnetJobDto dto);
    List<CardnetJobDto> listUnProcessedTransactions(LocalDateTime date);
}
