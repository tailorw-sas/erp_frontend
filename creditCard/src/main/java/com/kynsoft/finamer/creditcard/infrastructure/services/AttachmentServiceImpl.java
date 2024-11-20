package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.AttachmentResponse;
import com.kynsoft.finamer.creditcard.domain.dto.AttachmentDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IAttachmentService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.Attachment;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.AttachmentWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.AttachmentReadDataJPARepository;
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
public class AttachmentServiceImpl implements IAttachmentService {

    @Autowired
    private final AttachmentWriteDataJPARepository repositoryCommand;

    @Autowired
    private final AttachmentReadDataJPARepository repositoryQuery;

    public AttachmentServiceImpl(AttachmentWriteDataJPARepository repositoryCommand,
                                 AttachmentReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public AttachmentDto create(AttachmentDto dto) {
        Attachment entity = new Attachment(dto);
        return repositoryCommand.save(entity).toAggregate();
    }

    @Override
    public void update(AttachmentDto dto) {
        Attachment entity = new Attachment(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Attachment> specifications = new GenericSpecificationsBuilder<>(
                filterCriteria);
        Page<Attachment> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<Attachment> data) {
        List<AttachmentResponse> responseList = new ArrayList<>();
        for (Attachment entity : data.getContent()) {
            responseList.add(new AttachmentResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public void delete(AttachmentDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public AttachmentDto findById(UUID id) {
        Optional<Attachment> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND,
                new ErrorField("id", "The source not found.")));

    }

    @Override
    public List<AttachmentDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(Attachment::toAggregate).toList();
    }

    @Override
    public List<AttachmentDto> findAllByTransactionId(Long invoiceId) {
        return repositoryQuery.findAllByTransactionId(invoiceId).stream().map(Attachment::toAggregate).toList();
    }

    @Override
    public void create(List<AttachmentDto> dtos) {
        List<Attachment> attachments = new ArrayList<>();
        for(AttachmentDto dto : dtos){
            attachments.add(new Attachment(dto));
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
