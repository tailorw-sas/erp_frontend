package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import com.kynsoft.finamer.creditcard.domain.services.ICardNetJobService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.CardnetJobWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.CardnetJobReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CardNetJobServiceImpl implements ICardNetJobService {
    @Value("${redirect.private.key}")
    private String privateKey;

    @Autowired
    private CardnetJobWriteDataJPARepository repositoryCommand;

    @Autowired
    private CardnetJobReadDataJPARepository repositoryQuery;

    public CardNetJobServiceImpl(CardnetJobWriteDataJPARepository repositoryCommand){
        this.repositoryCommand = repositoryCommand;
    }

    @Override
    public UUID create(CardnetJobDto dto) {
        CardnetJob data = new CardnetJob(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(CardnetJobDto dto) {
        CardnetJob entity = new CardnetJob(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(entity);
    }

    @Override
    public CardnetJobDto findByTransactionId(UUID id) {
        Optional<CardnetJob> optional = this.repositoryQuery.findByTransactionId(id);
        if(optional.isPresent()){
            return optional.get().toAggregate();
        }else return null;

    }
}
