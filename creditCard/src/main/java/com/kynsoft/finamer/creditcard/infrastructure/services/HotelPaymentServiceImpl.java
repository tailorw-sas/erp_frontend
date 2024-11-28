package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.HotelPayment;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.HotelPaymentWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.HotelPaymentReadDataJPARepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return null;
    }

    @Override
    public HotelPaymentDto findById(UUID id) {
        return this.repositoryQuery.findById(id).map(HotelPayment::toAggregate).orElseThrow(()->
                new BusinessNotFoundException(new GlobalBusinessException( DomainErrorMessage.HOTEL_PAYMENT_NOT_FOUND, new ErrorField("id", DomainErrorMessage.HOTEL_PAYMENT_NOT_FOUND.getReasonPhrase()))));
    }
}
