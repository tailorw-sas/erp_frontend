package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageInvoiceTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceTypeService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageInvoiceType;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageInvoiceTypeWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageInvoiceTypeReadDataJPARepository;
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
public class ManageInvoiceTypeServiceImpl implements IManageInvoiceTypeService {

    @Autowired
    private final ManageInvoiceTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageInvoiceTypeReadDataJPARepository repositoryQuery;

    public ManageInvoiceTypeServiceImpl(ManageInvoiceTypeWriteDataJPARepository repositoryCommand, ManageInvoiceTypeReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageInvoiceTypeDto dto) {
        ManageInvoiceType entity = new ManageInvoiceType(dto);

        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageInvoiceTypeDto dto) {
        ManageInvoiceType entity = new ManageInvoiceType(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageInvoiceTypeDto dto) {
        ManageInvoiceType delete = new ManageInvoiceType(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode() + "-" + UUID.randomUUID());
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageInvoiceTypeDto findById(UUID id) {
        Optional<ManageInvoiceType> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_INVOICE_TYPE_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageInvoiceType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageInvoiceType> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageInvoiceType> data) {
        List<ManageInvoiceTypeResponse> responses = new ArrayList<>();
        for (ManageInvoiceType p : data.getContent()) {
            responses.add(new ManageInvoiceTypeResponse(p.toAggregate()));
        }

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
