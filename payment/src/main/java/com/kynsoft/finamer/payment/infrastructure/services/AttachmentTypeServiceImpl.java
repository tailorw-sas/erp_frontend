package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.AttachmentTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.infrastructure.identity.AttachmentType;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageAttachmentTypeWriteDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import com.kynsoft.finamer.payment.infrastructure.repository.query.AttachmentTypeReadDataJPARepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class AttachmentTypeServiceImpl implements IManageAttachmentTypeService {

    @Autowired
    private ManageAttachmentTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private AttachmentTypeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(AttachmentTypeDto dto) {
        AttachmentType data = new AttachmentType(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(AttachmentTypeDto dto) {
        AttachmentType update = new AttachmentType(dto);

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(AttachmentTypeDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public AttachmentTypeDto findById(UUID id) {
        Optional<AttachmentType> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.ATTACHMENT_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.ATTACHMENT_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public AttachmentTypeDto findByCode(String code) {
        Optional<AttachmentType> userSystem = this.repositoryQuery.findByCode(code);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.ATTACHMENT_TYPE_NOT_FOUND, new ErrorField("code", DomainErrorMessage.ATTACHMENT_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<AttachmentType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<AttachmentType> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
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

    private PaginatedResponse getPaginatedResponse(Page<AttachmentType> data) {
        List<AttachmentTypeResponse> responses = new ArrayList<>();
        for (AttachmentType p : data.getContent()) {
            responses.add(new AttachmentTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return this.repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public Long countByDefaultAndNotId(UUID id) {
        return this.repositoryQuery.countByDefaultAndNotId(id);
    }

    @Override
    public Long countByAntiToIncomeImportAndNotId(UUID id) {
        return this.repositoryQuery.countByAntiToIncomeImportAndNotId(id);
    }

}
