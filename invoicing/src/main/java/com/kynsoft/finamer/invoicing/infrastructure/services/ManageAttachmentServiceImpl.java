package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageAttachmentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAttachment;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageAttachmentWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageAttachmentReadDataJPARepository;
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
public class ManageAttachmentServiceImpl implements IManageAttachmentService {

    @Autowired
    private final ManageAttachmentWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageAttachmentReadDataJPARepository repositoryQuery;

    public ManageAttachmentServiceImpl(ManageAttachmentWriteDataJPARepository repositoryCommand,
            ManageAttachmentReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public Long create(ManageAttachmentDto dto) {
        ManageAttachment entity = new ManageAttachment(dto);
        return repositoryCommand.saveAndFlush(entity).getAttachmentId();
    }

    @Override
    public void update(ManageAttachmentDto dto) {
        ManageAttachment entity = new ManageAttachment(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageAttachment> specifications = new GenericSpecificationsBuilder<>(
                filterCriteria);
        Page<ManageAttachment> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageAttachment> data) {
        List<ManageAttachmentResponse> responseList = new ArrayList<>();
        for (ManageAttachment entity : data.getContent()) {
            responseList.add(new ManageAttachmentResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public void delete(ManageAttachmentDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageAttachmentDto findById(UUID id) {
        Optional<ManageAttachment> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND,
                new ErrorField("id", "The source not found.")));

    }

    @Override
    public List<ManageAttachmentDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageAttachment::toAggregate).toList();
    }

    @Override
    public void create(List<ManageAttachmentDto> dtos) {
        List<ManageAttachment> attachments = new ArrayList<>();
        for(ManageAttachmentDto dto : dtos){
            attachments.add(new ManageAttachment(dto));
        }
        this.repositoryCommand.saveAll(attachments);
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
