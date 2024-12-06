package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;

import java.util.List;

public class ImportBookingDuplicateValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageBookingService service;

    private final BookingImportCacheRedisRepository cacheRedisRepository;
    private final IManageHotelService manageHotelService;

    public ImportBookingDuplicateValidator(IManageBookingService service,
                                           BookingImportCacheRedisRepository cacheRedisRepository,
                                           IManageHotelService manageHotelService) {
        this.service = service;
        this.cacheRedisRepository = cacheRedisRepository;
        this.manageHotelService = manageHotelService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        String validate = obj.getHotelBookingNumber()
                        .split("\\s+")[obj.getHotelBookingNumber()
                        .split("\\s+").length - 1];
        ManageHotelDto hotel = manageHotelService.findByCode(obj.getManageHotelCode());
        //if (service.existByBookingHotelNumber(obj.getHotelBookingNumber()) ||
        if (service.existsByExactLastChars(validate, hotel.getId()) ||
                cacheRedisRepository.findBookingImportCacheByHotelBookingNumberAndImportProcessId(obj.getHotelBookingNumber(),obj.getImportProcessId()).isPresent()) {
            errorFieldList.add(new ErrorField("Hotel Booking Number", "Record has already been imported"));
            return false;
        }
        return true;
    }
}
