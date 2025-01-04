package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceStatusResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.invoicing.domain.services.IParameterizationService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoiceStatus;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageInvoiceStatusWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageInvoiceStatusReadDataJPARepository;
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
public class ManageInvoiceStatusServiceImpl implements IManageInvoiceStatusService {

    @Autowired
    private final ManageInvoiceStatusWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageInvoiceStatusReadDataJPARepository repositoryQuery;

    @Autowired
    private final IParameterizationService parameterizationService;

    public ManageInvoiceStatusServiceImpl(ManageInvoiceStatusWriteDataJPARepository repositoryCommand, ManageInvoiceStatusReadDataJPARepository repositoryQuery, IParameterizationService parameterizationService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.parameterizationService = parameterizationService;
    }

    @Override
    public UUID create(ManageInvoiceStatusDto dto) {
        ManageInvoiceStatus entity = new ManageInvoiceStatus(dto);

        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageInvoiceStatusDto dto) {
        ManageInvoiceStatus entity = new ManageInvoiceStatus(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageInvoiceStatusDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageInvoiceStatusDto findById(UUID id) {
        Optional<ManageInvoiceStatus> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_INVOICE_STATUS_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageInvoiceStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageInvoiceStatus> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public List<ManageInvoiceStatusDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageInvoiceStatus::toAggregate).toList();
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

    private PaginatedResponse getPaginatedResponse(Page<ManageInvoiceStatus> data) {
        List<ManageInvoiceStatusResponse> responseList = new ArrayList<>();
        for (ManageInvoiceStatus entity : data.getContent()) {
            responseList.add(new ManageInvoiceStatusResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<ManageInvoiceStatusDto> findAllToReplicate() {
        List<ManageInvoiceStatus> objects = this.repositoryQuery.findAll();
        List<ManageInvoiceStatusDto> objectDtos = new ArrayList<>();

        for (ManageInvoiceStatus object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

    @Override
    public ManageInvoiceStatusDto findByEInvoiceStatus(EInvoiceStatus invoiceStatus) {
        return switch (invoiceStatus) {
            case PROCECSED, PROCESSED ->
                this.repositoryQuery.findByProcessStatus()
                        .map(ManageInvoiceStatus::toAggregate)
                        .orElseThrow(() -> new BusinessException(
                                DomainErrorMessage.INVOICE_STATUS_NOT_FOUND,
                                DomainErrorMessage.INVOICE_STATUS_NOT_FOUND.getReasonPhrase()
                        ));
            case RECONCILED ->
                this.repositoryQuery.findByReconciledStatus()
                        .map(ManageInvoiceStatus::toAggregate)
                        .orElseThrow(() -> new BusinessException(
                                DomainErrorMessage.INVOICE_STATUS_NOT_FOUND,
                                DomainErrorMessage.INVOICE_STATUS_NOT_FOUND.getReasonPhrase()
                        ));
            case SENT ->
                this.repositoryQuery.findBySentStatus()
                        .map(ManageInvoiceStatus::toAggregate)
                        .orElseThrow(() -> new BusinessException(
                                DomainErrorMessage.INVOICE_STATUS_NOT_FOUND,
                                DomainErrorMessage.INVOICE_STATUS_NOT_FOUND.getReasonPhrase()
                        ));
            case CANCELED ->
                this.repositoryQuery.findByCanceledStatus()
                        .map(ManageInvoiceStatus::toAggregate)
                        .orElseThrow(() -> new BusinessException(
                                DomainErrorMessage.INVOICE_STATUS_NOT_FOUND,
                                DomainErrorMessage.INVOICE_STATUS_NOT_FOUND.getReasonPhrase()
                        ));
            //todo: no hay ningún caso de uso definido para este estado
            case PENDING -> null;
        };
    }

    @Override
    public ManageInvoiceStatusDto findByCode(String code) {
        ManageInvoiceStatus invoiceStatus = this.repositoryQuery.findByCode(code);

        if (invoiceStatus != null) {
            return invoiceStatus.toAggregate();
        }

        return null;
    }

    @Override
    public Long countBySentStatusAndNotId(UUID id) {
        return this.repositoryQuery.countBySentStatusAndNotId(id);
    }

    @Override
    public Long countByReconciledStatusAndNotId(UUID id) {
        return this.repositoryQuery.countByReconciledStatusAndNotId(id);
    }

    @Override
    public Long countByCanceledStatusAndNotId(UUID id) {
        return this.repositoryQuery.countByCanceledStatusAndNotId(id);
    }

    @Override
    public Long countByProcessStatusAndNotId(UUID id) {
        return this.repositoryQuery.countByProcessStatusAndNotId(id);
    }

    @Override
    public ManageInvoiceStatusDto findByCanceledStatus() {
        Optional<ManageInvoiceStatus> optionalEntity = repositoryQuery.findByCanceledStatus();

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_INVOICE_STATUS_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }
}
