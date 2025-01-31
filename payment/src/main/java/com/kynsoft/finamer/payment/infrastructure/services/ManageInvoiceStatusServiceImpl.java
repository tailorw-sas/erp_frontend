package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.search.ManageInvoiceStatusResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageInvoiceStatus;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageInvoiceStatusWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageInvoiceStatusReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;

@Service
public class ManageInvoiceStatusServiceImpl implements IManageInvoiceStatusService {

    @Autowired
    private final ManageInvoiceStatusWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageInvoiceStatusReadDataJPARepository repositoryQuery;

    public ManageInvoiceStatusServiceImpl(ManageInvoiceStatusWriteDataJPARepository repositoryCommand,
            ManageInvoiceStatusReadDataJPARepository repositoryQuery) {
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

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageInvoiceStatusDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageInvoiceStatusDto findById(UUID id) {
        Optional<ManageInvoiceStatus> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(
                DomainErrorMessage.MANAGE_INVOICE_STATUS_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public List<ManageInvoiceStatusDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageInvoiceStatus::toAggregate).toList();
    }

    @Override
    @Cacheable(value = "invoiceStatusCache", key = "#code", unless = "#result == null")
    public ManageInvoiceStatusDto findByCode(String code) {
        ManageInvoiceStatus invoiceStatus = this.repositoryQuery.findByCode(code);

        if (invoiceStatus != null) {
            return invoiceStatus.toAggregate();
        }

        return null;
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageInvoiceStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageInvoiceStatus> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageInvoiceStatus> data) {
        List<ManageInvoiceStatusResponse> responses = new ArrayList<>();
        for (ManageInvoiceStatus p : data.getContent()) {
            responses.add(new ManageInvoiceStatusResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
