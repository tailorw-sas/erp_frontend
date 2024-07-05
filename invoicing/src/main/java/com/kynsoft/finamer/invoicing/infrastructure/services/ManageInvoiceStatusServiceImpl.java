package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoiceStatus;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageInvoiceStatusWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageInvoiceStatusReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        // ManageInvoiceStatus delete = new ManageInvoiceStatus(dto);

        // delete.setDeleted(Boolean.TRUE);
        // delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        // delete.setStatus(Status.INACTIVE);
        // delete.setDeletedAt(LocalDateTime.now());
        //
        // repositoryCommand.save(delete);
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

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

        }
    }

    @Override
    public ManageInvoiceStatusDto findByCode(String code) {
        ManageInvoiceStatus invoiceStatus = this.repositoryQuery.findByCode(code);

        if (invoiceStatus != null) {
            return invoiceStatus.toAggregate();
        }

        return null;
    }

}
