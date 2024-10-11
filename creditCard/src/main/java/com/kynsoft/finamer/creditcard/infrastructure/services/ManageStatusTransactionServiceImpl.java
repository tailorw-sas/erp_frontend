package com.kynsoft.finamer.creditcard.infrastructure.services;

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

        return optionalEntity.map(CardnetJob::toAggregate).orElse(null);
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
