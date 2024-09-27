package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ParameterizationDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IParameterizationService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoiceType;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageInvoiceTypeWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageInvoiceTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageInvoiceTypeServiceImpl implements IManageInvoiceTypeService {

    @Autowired
    private final ManageInvoiceTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageInvoiceTypeReadDataJPARepository repositoryQuery;

    @Autowired
    private final IParameterizationService parameterizationService;

    public ManageInvoiceTypeServiceImpl(ManageInvoiceTypeWriteDataJPARepository repositoryCommand, ManageInvoiceTypeReadDataJPARepository repositoryQuery, IParameterizationService parameterizationService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.parameterizationService = parameterizationService;
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
//        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageInvoiceTypeDto findById(UUID id) {
        Optional<ManageInvoiceType> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_INVOICE_TYPE_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }



    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public ManageInvoiceTypeDto findByEInvoiceType(EInvoiceType invoiceType) {
        ParameterizationDto parameterization = this.parameterizationService.findActiveParameterization();
        ManageInvoiceTypeDto invoiceTypeDto = null;
        if(parameterization != null){
            invoiceTypeDto = switch (invoiceType) {
                case CREDIT -> this.findByCode(parameterization.getTypeCredit());
                case INVOICE -> this.findByCode(parameterization.getTypeInvoice());
                case INCOME -> this.findByCode(parameterization.getTypeIncome());
                case OLD_CREDIT -> this.findByCode(parameterization.getTypeOldCredit());
            };
        }
        return invoiceTypeDto;
    }

    @Override
    public ManageInvoiceTypeDto findByCode(String code) {
        return this.repositoryQuery.findByCode(code).map(ManageInvoiceType::toAggregate).orElse(null);
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
