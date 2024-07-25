package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportCacheRedisRepository;

import java.util.List;
import java.util.Optional;

public class ImportBookingDuplicateValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageBookingService service;

    private final BookingImportCacheRedisRepository cacheRedisRepository;

    public ImportBookingDuplicateValidator(IManageBookingService service,
                                           BookingImportCacheRedisRepository cacheRedisRepository) {
        this.service = service;
        this.cacheRedisRepository = cacheRedisRepository;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (service.existByBookingHotelNumber(obj.getHotelBookingNumber()) ||
                cacheRedisRepository.findBookingImportCacheByHotelBookingNumber(obj.getHotelBookingNumber()).isPresent()) {
            errorFieldList.add(new ErrorField("Hotel Booking Number", "Record has already been imported"));
            return false;
        }
        return true;
    }
}
