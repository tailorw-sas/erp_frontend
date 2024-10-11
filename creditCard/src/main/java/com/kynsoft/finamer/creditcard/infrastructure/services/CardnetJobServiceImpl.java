package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import com.kynsoft.finamer.creditcard.domain.services.ICardnetJobServeice;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.CardnetJobWriteDataJPARepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CardnetJobServiceImpl implements ICardnetJobServeice {

    private final CardnetJobWriteDataJPARepository repositoryCommand;

    public CardnetJobServiceImpl(CardnetJobWriteDataJPARepository repositoryCommand){
        this.repositoryCommand = repositoryCommand;
    }
    @Override
    public void update(CardnetJobDto dto) {
        CardnetJob entity = new CardnetJob(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(entity);
    }

}
