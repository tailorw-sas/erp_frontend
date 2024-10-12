package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoiceTransactionType;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageInvoiceTransactionTypeWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageInvoiceTransactionTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
