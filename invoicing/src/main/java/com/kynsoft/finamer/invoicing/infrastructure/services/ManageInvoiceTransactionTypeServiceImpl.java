package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceTransactionTypeResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoiceTransactionType;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageInvoiceTransactionTypeWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageInvoiceTransactionTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageInvoiceTransactionTypeDto dto) {
        ManageInvoiceTransactionType delete = new ManageInvoiceTransactionType(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setDeletedAt(LocalDateTime.now());
        delete.setDefaults(false);

        repositoryCommand.save(delete);
    }

    @Override
    public ManageInvoiceTransactionTypeDto findById(UUID id) {
        Optional<ManageInvoiceTransactionType> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_INVOICE_TRANSACTION_TYPE_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }



    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public ManageInvoiceTransactionTypeDto findByDefaults() {
        return this.repositoryQuery.findByDefaults().map(ManageInvoiceTransactionType::toAggregate).orElse(null);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {

        GenericSpecificationsBuilder<ManageInvoiceTransactionType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageInvoiceTransactionType> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageInvoiceTransactionType> data) {
        List<ManageInvoiceTransactionTypeResponse> userSystemsResponses = new ArrayList<>();
        for (ManageInvoiceTransactionType p : data.getContent()) {
            userSystemsResponses.add(new ManageInvoiceTransactionTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
