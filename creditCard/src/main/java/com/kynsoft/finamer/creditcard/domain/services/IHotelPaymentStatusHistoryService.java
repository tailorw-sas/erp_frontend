package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentStatusHistoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IHotelPaymentStatusHistoryService {

    HotelPaymentStatusHistoryDto create(HotelPaymentStatusHistoryDto dto);

    HotelPaymentStatusHistoryDto create(HotelPaymentDto hotelPaymentDto, String employee);

    HotelPaymentStatusHistoryDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
