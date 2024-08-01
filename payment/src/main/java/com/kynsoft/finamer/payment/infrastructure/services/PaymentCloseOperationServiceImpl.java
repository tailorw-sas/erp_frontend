package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentCloseOperationResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentCloseOperation;
import com.kynsoft.finamer.payment.infrastructure.repository.command.PaymentCloseOperationWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.PaymentCloseOperationReadDataJPARepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentCloseOperationServiceImpl implements IPaymentCloseOperationService {

    @Autowired
    private PaymentCloseOperationWriteDataJPARepository repositoryCommand;

    @Autowired
    private PaymentCloseOperationReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(PaymentCloseOperationDto dto) {
        PaymentCloseOperation data = new PaymentCloseOperation(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(PaymentCloseOperationDto dto) {
        PaymentCloseOperation update = new PaymentCloseOperation(dto);

        update.setUpdatedAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void updateAll(List<PaymentCloseOperationDto> dtos) {
        List<PaymentCloseOperation> updates = new ArrayList<>();

        for (PaymentCloseOperationDto dto : dtos) {
            PaymentCloseOperation up = new PaymentCloseOperation(dto);
            up.setUpdatedAt(LocalDateTime.now());
            updates.add(up);
        }

        this.repositoryCommand.saveAll(updates);
    }

    @Override
    public void delete(PaymentCloseOperationDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public PaymentCloseOperationDto findById(UUID id) {
        Optional<PaymentCloseOperation> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND.getReasonPhrase())));
    }


    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<PaymentCloseOperation> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<PaymentCloseOperation> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<PaymentCloseOperation> data) {
        List<PaymentCloseOperationResponse> responses = new ArrayList<>();
        for (PaymentCloseOperation p : data.getContent()) {
            responses.add(new PaymentCloseOperationResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long findByHotelId(UUID hotelId) {
        return this.repositoryQuery.findByHotelId(hotelId);
    }


    @Override
    public List<PaymentCloseOperationDto> findByHotelIds(List<UUID> hotelIds) {
        return this.repositoryQuery.findByHotelIds(hotelIds)
                .stream()
                .map(PaymentCloseOperation::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentCloseOperationDto findByHotelIds(UUID hotel) {
        Optional<PaymentCloseOperation> object = this.repositoryQuery.findByHotelIds(hotel);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND.getReasonPhrase())));
    }

}
