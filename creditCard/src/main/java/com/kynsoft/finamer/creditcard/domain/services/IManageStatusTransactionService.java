package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.application.query.objectResponse.CardNetTransactionDataResponse;
import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import reactor.core.publisher.Mono;

public interface IManageStatusTransactionService {
    CardnetJobDto findBySession(String session);
    Mono<CardNetTransactionDataResponse> dataTransactionSuccess(String url);
}
