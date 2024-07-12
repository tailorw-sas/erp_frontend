package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.InvoiceCloseOperationResponse;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.InvoiceCloseOperation;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.InvoiceCloseOperationWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.InvoiceCloseOperationReadDataJPARepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceCloseOperationServiceImpl implements IInvoiceCloseOperationService {

    @Autowired
    private InvoiceCloseOperationWriteDataJPARepository repositoryCommand;

    @Autowired
    private InvoiceCloseOperationReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(InvoiceCloseOperationDto dto) {
        InvoiceCloseOperation data = new InvoiceCloseOperation(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(InvoiceCloseOperationDto dto) {
        InvoiceCloseOperation update = new InvoiceCloseOperation(dto);

        update.setUpdatedAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(InvoiceCloseOperationDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public InvoiceCloseOperationDto findById(UUID id) {
        Optional<InvoiceCloseOperation> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<InvoiceCloseOperation> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<InvoiceCloseOperation> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<InvoiceCloseOperation> data) {
        List<InvoiceCloseOperationResponse> responses = new ArrayList<>();
        for (InvoiceCloseOperation p : data.getContent()) {
            responses.add(new InvoiceCloseOperationResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long findByHotelId(UUID hotelId) {
        return this.repositoryQuery.findByHotelId(hotelId);
    }

}
