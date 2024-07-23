package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageInvoiceTransactionTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageInvoiceTransactionType;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageInvoiceTransactionTypeWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageInvoiceTransactionTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ManageInvoiceTransactionTypeServiceImpl implements IManageInvoiceTransactionTypeService {

    @Autowired
    private final ManageInvoiceTransactionTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageInvoiceTransactionTypeReadDataJPARepository repositoryQuery;

    public ManageInvoiceTransactionTypeServiceImpl(ManageInvoiceTransactionTypeWriteDataJPARepository repositoryCommand, ManageInvoiceTransactionTypeReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageInvoiceTransactionTypeDto dto) {
        ManageInvoiceTransactionType entity = new ManageInvoiceTransactionType(dto);

        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageInvoiceTransactionTypeDto dto) {
        ManageInvoiceTransactionType entity = new ManageInvoiceTransactionType(dto);

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageInvoiceTransactionTypeDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageInvoiceTransactionTypeDto findById(UUID id) {
        Optional<ManageInvoiceTransactionType> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_INVOICE_TRANSACTION_TYPE_NOT_FOUND, new ErrorField("id", "The Invoice Transaction Type not found.")));
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageInvoiceTransactionType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageInvoiceTransactionType> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageInvoiceTransactionType> data) {
        List<ManageInvoiceTransactionTypeResponse> responses = new ArrayList<>();
        for (ManageInvoiceTransactionType p : data.getContent()) {
            responses.add(new ManageInvoiceTransactionTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
