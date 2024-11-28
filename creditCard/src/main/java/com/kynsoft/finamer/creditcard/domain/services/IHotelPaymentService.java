package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IHotelPaymentService {

    HotelPaymentDto create(HotelPaymentDto dto);

    void update(HotelPaymentDto dto);

    void delete(HotelPaymentDto dto);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    HotelPaymentDto findById(UUID id);
}
