package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.HotelPaymentStatusHistoryResponse;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentStatusHistoryDto;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentStatusHistoryService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.HotelPaymentStatusHistory;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.HotelPaymentStatusHistoryWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.HotelPaymentStatusHistoryReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HotelPaymentStatusHistoryServiceImpl implements IHotelPaymentStatusHistoryService {

    private final HotelPaymentStatusHistoryWriteDataJPARepository repositoryCommand;

    private final HotelPaymentStatusHistoryReadDataJPARepository repositoryQuery;

    public HotelPaymentStatusHistoryServiceImpl(HotelPaymentStatusHistoryWriteDataJPARepository repositoryCommand, HotelPaymentStatusHistoryReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public HotelPaymentStatusHistoryDto create(HotelPaymentStatusHistoryDto dto) {
        HotelPaymentStatusHistory entity = new HotelPaymentStatusHistory(dto);
        return this.repositoryCommand.save(entity).toAggregate();
    }

    @Override
    public HotelPaymentStatusHistoryDto create(HotelPaymentDto hotelPaymentDto, String employee) {
        HotelPaymentStatusHistoryDto dto = new HotelPaymentStatusHistoryDto(
                UUID.randomUUID(),
                "The hotel payment status is "+hotelPaymentDto.getStatus().getCode()+"-"+hotelPaymentDto.getStatus().getName()+".",
                null,
                employee,
                hotelPaymentDto,
                hotelPaymentDto.getStatus()
        );
        return this.create(dto);
    }

    @Override
    public HotelPaymentStatusHistoryDto findById(UUID id) {
        return this.repositoryQuery.findById(id).map(HotelPaymentStatusHistory::toAggregate).orElseThrow(()->
            new BusinessNotFoundException(
                    new GlobalBusinessException(DomainErrorMessage.HOTEL_PAYMENT_STATUS_HISTORY_NOT_FOUND,
                            new ErrorField("id", DomainErrorMessage.HOTEL_PAYMENT_STATUS_HISTORY_NOT_FOUND.getReasonPhrase())))
        );
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<HotelPaymentStatusHistory> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<HotelPaymentStatusHistory> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<HotelPaymentStatusHistory> data) {
        List<HotelPaymentStatusHistoryResponse> responses = new ArrayList<>();
        for (HotelPaymentStatusHistory p : data.getContent()) {
            responses.add(new HotelPaymentStatusHistoryResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
