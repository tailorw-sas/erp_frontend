package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentSource;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManagePaymentSourceWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManagePaymentSourceReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManagePaymentSourceServiceImpl implements IManagePaymentSourceService {

    @Autowired
    private ManagePaymentSourceReadDataJPARepository repositoryQuery;

    @Autowired
    private ManagePaymentSourceWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManagePaymentSourceDto dto) {
        ManagePaymentSource entity = new ManagePaymentSource(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManagePaymentSourceDto dto) {
        ManagePaymentSource update = new ManagePaymentSource(dto);
        repositoryCommand.save(update);
    }

    @Override
    public void delete(ManagePaymentSourceDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagePaymentSourceDto findById(UUID id) {
        Optional<ManagePaymentSource> optionalEntity = repositoryQuery.findById(id);
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND, new ErrorField("id", "The manager payment source not found.")));
    }
}
