package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ResourceTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import com.kynsoft.finamer.payment.infrastructure.identity.MaganeResourceType;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageResourceTypeWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ResourceTypeReadDataJPARepository;
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
public class ResourceTypeServiceImpl implements IManageResourceTypeService {

    @Autowired
    private ManageResourceTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private ResourceTypeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ResourceTypeDto dto) {
        MaganeResourceType data = new MaganeResourceType(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ResourceTypeDto dto) {
        MaganeResourceType update = new MaganeResourceType(dto);

        update.setUpdatedAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ResourceTypeDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ResourceTypeDto findById(UUID id) {
        Optional<MaganeResourceType> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.RESOURCE_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.RESOURCE_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public ResourceTypeDto findByCode(String code) {
        Optional<MaganeResourceType> resourceType = this.repositoryQuery.findResourceTypeByCodeAndStatus(code, Status.ACTIVE);
        if (resourceType.isPresent()) {
            return resourceType.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.RESOURCE_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.RESOURCE_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<MaganeResourceType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<MaganeResourceType> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<MaganeResourceType> data) {
        List<ResourceTypeResponse> responses = new ArrayList<>();
        for (MaganeResourceType p : data.getContent()) {
            responses.add(new ResourceTypeResponse(p.toAggregate()));
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
    public List<ResourceTypeDto> findAllToReplicate() {
        List<MaganeResourceType> objects = this.repositoryQuery.findAll();
        List<ResourceTypeDto> objectDtos = new ArrayList<>();

        for (MaganeResourceType object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

    @Override
    public Long countByInvoiceAndNotId(UUID id) {
        return this.repositoryQuery.countByInvoiceAndNotId(id);
    }

    @Override
    public ResourceTypeDto getByDefault() {
        Optional<MaganeResourceType> userSystem = this.repositoryQuery.getByDefault();
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.RESOURCE_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.RESOURCE_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public Long countByVccAndNotId(UUID id) {
        return this.repositoryQuery.countByVccAndNotId(id);
    }

}
