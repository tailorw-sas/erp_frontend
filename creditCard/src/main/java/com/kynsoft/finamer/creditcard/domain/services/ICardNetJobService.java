package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;

import java.util.UUID;

public interface ICardNetJobService {
    UUID create(CardnetJobDto dto);
    CardnetJobDto findByTransactionId(UUID id);
    void update(CardnetJobDto dto);
}
