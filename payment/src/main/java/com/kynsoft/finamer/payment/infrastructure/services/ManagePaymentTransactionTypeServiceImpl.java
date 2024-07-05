package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentTransactionType;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManagePaymentTransactionTypeWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManagePaymentTransactionTypeReadDataJPARepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManagePaymentTransactionTypeServiceImpl implements IManagePaymentTransactionTypeService {

    @Autowired
    private ManagePaymentTransactionTypeReadDataJPARepository repositoryQuery;

    @Autowired
    private ManagePaymentTransactionTypeWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManagePaymentTransactionTypeDto dto) {
        ManagePaymentTransactionType entity = new ManagePaymentTransactionType(dto);
        ManagePaymentTransactionType saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManagePaymentTransactionTypeDto dto) {
        repositoryCommand.save(new ManagePaymentTransactionType(dto));
    }

    @Override
    public void delete(ManagePaymentTransactionTypeDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagePaymentTransactionTypeDto findById(UUID id) {
        Optional<ManagePaymentTransactionType> optionalEntity = repositoryQuery.findById(id);
        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_NOT_FOUND, new ErrorField("id", "The manage payment transaction type not found.")));
    }
}
