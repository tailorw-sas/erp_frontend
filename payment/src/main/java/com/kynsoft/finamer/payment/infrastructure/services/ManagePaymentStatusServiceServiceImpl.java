package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentStatus;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManagePaymentStatusWriteDataJpaRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManagePaymentStatusReadDataJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManagePaymentStatusServiceServiceImpl implements IManagePaymentStatusService {

    @Autowired
    private ManagePaymentStatusWriteDataJpaRepository repositoryCommand;

    @Autowired
    private ManagePaymentStatusReadDataJpaRepository repositoryQuery;

    @Override
    public UUID create(ManagePaymentStatusDto dto) {
        ManagePaymentStatus entity = new ManagePaymentStatus(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManagePaymentStatusDto dto) {
        ManagePaymentStatus entity = new ManagePaymentStatus(dto);
        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManagePaymentStatusDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagePaymentStatusDto findById(UUID id) {
        Optional<ManagePaymentStatus> result = this.repositoryQuery.findById(id);
        if (result.isPresent()) {
            return result.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_PAYMENT_STATUS_NOT_FOUND, new ErrorField("code", "Payment Status code not found.")));
    }
}
