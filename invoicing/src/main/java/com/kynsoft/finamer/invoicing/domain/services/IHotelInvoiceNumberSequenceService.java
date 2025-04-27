package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IHotelInvoiceNumberSequenceService {
    UUID create(HotelInvoiceNumberSequenceDto dto);

    void update(HotelInvoiceNumberSequenceDto dto);

    HotelInvoiceNumberSequenceDto findById(UUID id);

    HotelInvoiceNumberSequenceDto getByHotelCodeAndInvoiceType(String code, EInvoiceType invoiceType);

    HotelInvoiceNumberSequenceDto getByTradingCompanyCodeAndInvoiceType(String code, EInvoiceType invoiceType);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    long incrementAndGetByHotel(String hotelCode, EInvoiceType invoiceType);

    long incrementAndGetByTradingCompany(String tradingCompanyCode, EInvoiceType invoiceType);
}
