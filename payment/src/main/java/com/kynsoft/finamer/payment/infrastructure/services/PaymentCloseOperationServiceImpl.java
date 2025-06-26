package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentCloseOperationResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentCloseOperation;
import com.kynsoft.finamer.payment.infrastructure.repository.command.PaymentCloseOperationWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.PaymentCloseOperationReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

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
    public Long countByHotelId(UUID hotelId) {
        return this.repositoryQuery.findByHotelId(hotelId);
    }


    @Override
    public List<PaymentCloseOperationDto> findByHotelId(List<UUID> hotelIds) {
        return this.repositoryQuery.findByHotelIds(hotelIds)
                .stream()
                .map(PaymentCloseOperation::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentCloseOperationDto findByHotelId(UUID hotel) {
        Optional<PaymentCloseOperation> object = this.repositoryQuery.findByHotelIds(hotel);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    @Cacheable(cacheNames = "closeOperationByHotel", unless = "#result == null")
    public PaymentCloseOperationDto findByHotelIdsCacheable(UUID hotel) {
        Optional<PaymentCloseOperation> object = this.repositoryQuery.findByHotelIds(hotel);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND.getReasonPhrase())));
    }

    @CacheEvict(allEntries = true, value = "closeOperationByHotel")
    @Override
    public void clearCache() {
        System.out.println("Clearing closeOperationByHotel cache");
    }

    @Override
    public Map<UUID, PaymentCloseOperationDto> getMapByHotelId(List<UUID> hotelIds) {
        if(Objects.isNull(hotelIds)){
            throw new IllegalArgumentException("The hotel ID list must not be null");
        }
        return this.findByHotelId(hotelIds).stream()
                .collect(Collectors.toMap(closeOperationDto -> closeOperationDto.getHotel().getId(), closeOperation -> closeOperation));
    }

    @Override
    public OffsetDateTime getTransactionDate(PaymentCloseOperationDto closeOperationDto) {
        ZoneId zone = ZoneId.systemDefault();

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(zone);
        }
        return closeOperationDto.getEndDate().atTime(LocalTime.now()).atZone(zone).toOffsetDateTime();
    }

}
