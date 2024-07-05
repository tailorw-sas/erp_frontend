package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;

import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAttachmentType;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageAttachmentTypeWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageAttachmentTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageAttachmentTypeServiceImpl implements IManageAttachmentTypeService {

    @Autowired
    private final ManageAttachmentTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageAttachmentTypeReadDataJPARepository repositoryQuery;

    public ManageAttachmentTypeServiceImpl(ManageAttachmentTypeWriteDataJPARepository repositoryCommand,
            ManageAttachmentTypeReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageAttachmentTypeDto dto) {
        ManageAttachmentType entity = new ManageAttachmentType(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageAttachmentTypeDto dto) {
        ManageAttachmentType entity = new ManageAttachmentType(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageAttachmentTypeDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageAttachmentTypeDto findById(UUID id) {
        Optional<ManageAttachmentType> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.MANAGE_ATTACHMENT_TYPE_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    Status enumValue = Status.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Status: " + filter.getValue());
                }
            }
        }
    }

}
