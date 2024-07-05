package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageCreditCardType;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageCreditCardTypeWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageCreditCardTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageCreditCardTypeServiceImpl implements IManageCreditCardTypeService {

    @Autowired
    private ManageCreditCardTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageCreditCardTypeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageCreditCardTypeDto dto) {
        ManageCreditCardType data = new ManageCreditCardType(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageCreditCardTypeDto dto) {
        ManageCreditCardType update = new ManageCreditCardType(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageCreditCardTypeDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageCreditCardTypeDto findById(UUID id) {
        Optional<ManageCreditCardType> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_BANK_NOT_FOUND, new ErrorField("id", "Manager Bank not found.")));
    }

}
