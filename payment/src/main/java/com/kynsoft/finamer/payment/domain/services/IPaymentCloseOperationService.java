package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPaymentCloseOperationService {
    UUID create(PaymentCloseOperationDto dto);

    void update(PaymentCloseOperationDto dto);

    void delete(PaymentCloseOperationDto dto);

    PaymentCloseOperationDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long findByHotelId(UUID hotelId);
}
