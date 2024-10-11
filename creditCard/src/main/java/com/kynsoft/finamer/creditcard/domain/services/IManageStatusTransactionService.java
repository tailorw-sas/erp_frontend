package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import reactor.core.publisher.Mono;

public interface IManageStatusTransactionService {
    CardnetJobDto findBySession(String session);
    Mono<String> dataTransactionSuccess(String url);
}
