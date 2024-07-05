package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageAttachmentType;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageAttachmentTypeWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageAttachmentTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManageAttachmentTypeServiceImpl implements IManageAttachmentTypeService {

    @Autowired
    private ManageAttachmentTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageAttachmentTypeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageAttachmentTypeDto dto) {
        ManageAttachmentType data = new ManageAttachmentType(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageAttachmentTypeDto dto) {
        ManageAttachmentType update = new ManageAttachmentType(dto);

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageAttachmentTypeDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageAttachmentTypeDto findById(UUID id) {
        Optional<ManageAttachmentType> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Manage Attachment Type not found.")));
    }

}
