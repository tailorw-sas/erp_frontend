package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.CreditCardCloseOperationDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ICreditCardCloseOperationService {
    UUID create(CreditCardCloseOperationDto dto);

    void update(CreditCardCloseOperationDto dto);

    void updateAll(List<CreditCardCloseOperationDto> dtos);

    void delete(CreditCardCloseOperationDto dto);

    CreditCardCloseOperationDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long findByHotelId(UUID hotelId);

    List<CreditCardCloseOperationDto> findByHotelIds(List<UUID> hotelIds);

    CreditCardCloseOperationDto findActiveByHotelId(UUID hotelId);
}
