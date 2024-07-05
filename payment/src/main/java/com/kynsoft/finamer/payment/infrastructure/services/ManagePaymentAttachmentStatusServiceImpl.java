package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentAttachmentStatus;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManagePaymentAttachmentStatusWriteDataJpaRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManagePaymentAttachmentStatusReadDataJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManagePaymentAttachmentStatusServiceImpl implements IManagePaymentAttachmentStatusService {
    
    @Autowired
    private ManagePaymentAttachmentStatusWriteDataJpaRepository repositoryCommand;
    
    @Autowired
    private ManagePaymentAttachmentStatusReadDataJpaRepository repositoryQuery;
    
    @Override
    public UUID create(ManagePaymentAttachmentStatusDto dto) {
        final ManagePaymentAttachmentStatus entity = new ManagePaymentAttachmentStatus(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManagePaymentAttachmentStatusDto dto) {
        ManagePaymentAttachmentStatus entity = new ManagePaymentAttachmentStatus(dto);
        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManagePaymentAttachmentStatusDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagePaymentAttachmentStatusDto findById(UUID id) {
        Optional<ManagePaymentAttachmentStatus> result = this.repositoryQuery.findById(id);
        if (result.isPresent()) {
            return result.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_PAYMENT_ATTACHMENT_STATUS_NOT_FOUND, new ErrorField("code", "Payment Attachment Status code not found.")));
    }
}
