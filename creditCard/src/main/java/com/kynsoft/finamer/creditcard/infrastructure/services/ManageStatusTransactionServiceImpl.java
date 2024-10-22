package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.CardNetTransactionDataResponse;
import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageStatusTransactionService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.CardnetJobReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Service
public class ManageStatusTransactionServiceImpl implements IManageStatusTransactionService {

    @Autowired
    private final CardnetJobReadDataJPARepository repositoryCardnetJobQuery;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public ManageStatusTransactionServiceImpl(CardnetJobReadDataJPARepository repositoryCardnetJobQuery,
                                              WebClient.Builder webClientBuilder){
       this.repositoryCardnetJobQuery = repositoryCardnetJobQuery;
       this.webClientBuilder = webClientBuilder;

    }
    @Override
    public CardnetJobDto findBySession(String session) {
        Optional<CardnetJob> optionalEntity = repositoryCardnetJobQuery.findBySession(session);
        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND, new ErrorField("session", DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND.getReasonPhrase())));
    }
    @Override
    public Mono<CardNetTransactionDataResponse> dataTransactionSuccess(String url) {

            return webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(CardNetTransactionDataResponse.class)
                    .timeout(Duration.ofSeconds(10));
        }
}
