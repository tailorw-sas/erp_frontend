package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageInvoiceStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageInvoiceStatus;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageInvoiceStatusWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageInvoiceStatusReadDataJPARepository;
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

    public ManageInvoiceStatusServiceImpl(ManageInvoiceStatusWriteDataJPARepository repositoryCommand, ManageInvoiceStatusReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
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

        repositoryCommand.save(entity);    }

    @Override
    public void delete(ManageInvoiceStatusDto dto) {
        ManageInvoiceStatus delete = new ManageInvoiceStatus(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setStatus(Status.INACTIVE);
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageInvoiceStatusDto findById(UUID id) {
        Optional<ManageInvoiceStatus> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
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
        return repositoryQuery.countByCodeAndNotId(code,id);
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
}
