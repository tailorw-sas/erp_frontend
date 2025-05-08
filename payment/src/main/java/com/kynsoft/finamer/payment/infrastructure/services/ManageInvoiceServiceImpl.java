package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageInvoiceResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.payment.infrastructure.identity.Invoice;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageInvoiceWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageInvoiceReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageInvoiceServiceImpl implements IManageInvoiceService {

    @Autowired
    private ManageInvoiceWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageInvoiceReadDataJPARepository repositoryQuery;

    @Override
    public void create(ManageInvoiceDto dto) {
        this.repositoryCommand.save(new Invoice(dto));
    }

    @Override
    public void update(ManageInvoiceDto dto) {
        this.repositoryCommand.save(new Invoice(dto));
    }

    @Override
    public ManageInvoiceDto findById(UUID id) {
        Optional<Invoice> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INVOICE_NOT_FOUND_, new ErrorField("id", DomainErrorMessage.INVOICE_NOT_FOUND_.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Invoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Invoice> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<Invoice> data) {
        List<ManageInvoiceResponse> responses = new ArrayList<>();
        for (Invoice p : data.getContent()) {
            responses.add(new ManageInvoiceResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public void deleteAll() {
        this.repositoryCommand.deleteAll();
    }

    @Override
    public List<ManageInvoiceDto> findByIdIn(List<UUID> ids) {
        List<ManageInvoiceDto> list = new ArrayList<>();
        for (Invoice invoice : this.repositoryQuery.findByIdIn(ids)) {
            list.add(invoice.toAggregateApplyPayment());
        }
        return list;
    }

    @Override
    public List<ManageInvoiceDto> findSortedInvoicesByIdIn(List<UUID> ids) {
        //return this.repositoryQuery.findInvoiceWithEntityGraphByIdIn(ids).stream()
        List<Invoice> invoices = this.repositoryQuery.findAllByIdInCustom(ids);
        return invoices.stream()
                .map(Invoice::toAggregate)
                .sorted(Comparator.comparingDouble(ManageInvoiceDto::getInvoiceAmount))
                .collect(Collectors.toList());
    }

    @Override
    public List<ManageInvoiceDto> findInvoicesByGenId(List<Long> ids) {
        if(Objects.isNull(ids)){
            throw new IllegalArgumentException("The invoiceId list must not be null");
        }

        return repositoryQuery.findInvoiceByInvoiceIdIn(ids).stream()
                .map(Invoice::toAggregate)
                .collect(Collectors.toList());
    }

}
