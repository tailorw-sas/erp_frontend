package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.HotelPaymentResponse;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.HotelPayment;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.HotelPaymentWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.HotelPaymentReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HotelPaymentServiceImpl implements IHotelPaymentService {

    private final HotelPaymentWriteDataJPARepository repositoryCommand;

    private final HotelPaymentReadDataJPARepository repositoryQuery;

    public HotelPaymentServiceImpl(HotelPaymentWriteDataJPARepository repositoryCommand, HotelPaymentReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public HotelPaymentDto create(HotelPaymentDto dto) {
        HotelPayment entity = new HotelPayment(dto);
        return this.repositoryCommand.save(entity).toAggregate();
    }

    @Override
    public void update(HotelPaymentDto dto) {
        HotelPayment entity = new HotelPayment(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        this.repositoryCommand.save(entity).toAggregate();
    }

    @Override
    public void delete(HotelPaymentDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<HotelPayment> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<HotelPayment> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public HotelPaymentDto findById(UUID id) {
        return this.repositoryQuery.findById(id).map(HotelPayment::toAggregate).orElseThrow(()->
                new BusinessNotFoundException(new GlobalBusinessException( DomainErrorMessage.HOTEL_PAYMENT_NOT_FOUND, new ErrorField("id", DomainErrorMessage.HOTEL_PAYMENT_NOT_FOUND.getReasonPhrase()))));
    }

    private PaginatedResponse getPaginatedResponse(Page<HotelPayment> data) {
        List<HotelPaymentResponse> responseList = new ArrayList<>();
        for (HotelPayment entity : data.getContent()) {
            responseList.add(new HotelPaymentResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
