package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentStatusHistoryResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentStatusHistory;
import com.kynsoft.finamer.payment.infrastructure.repository.command.PaymentStatusHistoryWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.PaymentStatusHistoryReadDataJPARepository;
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
public class PaymentStatusHistoryServiceImpl implements IPaymentStatusHistoryService {

    @Autowired
    private PaymentStatusHistoryWriteDataJPARepository repositoryCommand;

    @Autowired
    private PaymentStatusHistoryReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(PaymentStatusHistoryDto dto) {
        PaymentStatusHistory data = new PaymentStatusHistory(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(PaymentStatusHistoryDto dto) {
        PaymentStatusHistory update = new PaymentStatusHistory(dto);

        update.setUpdatedAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(PaymentStatusHistoryDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public PaymentStatusHistoryDto findById(UUID id) {
        Optional<PaymentStatusHistory> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.PAYMENT_ATTACHMENT_STATUS_HISTORY_NOT_FOUND, new ErrorField("id", DomainErrorMessage.PAYMENT_ATTACHMENT_STATUS_HISTORY_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<PaymentStatusHistory> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<PaymentStatusHistory> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<PaymentStatusHistory> data) {
        List<PaymentStatusHistoryResponse> responses = new ArrayList<>();
        for (PaymentStatusHistory p : data.getContent()) {
            responses.add(new PaymentStatusHistoryResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
