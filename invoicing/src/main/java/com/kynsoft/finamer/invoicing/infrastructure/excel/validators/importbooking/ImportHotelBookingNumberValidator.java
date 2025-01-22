package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;

import java.util.List;

public class ImportHotelBookingNumberValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageHotelService manageHotelService;
    private final IManageBookingService manageBookingService;

    private final BookingImportCacheRedisRepository cacheRedisRepository;

    public ImportHotelBookingNumberValidator(IManageHotelService manageHotelService,
            IManageBookingService manageBookingService,
            BookingImportCacheRedisRepository cacheRedisRepository) {
        this.manageHotelService = manageHotelService;
        this.manageBookingService = manageBookingService;
        this.cacheRedisRepository = cacheRedisRepository;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {

        List<BookingImportCache> list = this.cacheRedisRepository.findBookingImportCacheByHotelBookingNumberAndImportProcessId(obj.getHotelBookingNumber(), obj.getImportProcessId());
        if (this.isValidInsertion(list, obj)) {
            errorFieldList.add(new ErrorField("HotelBookingNumber", "The Hotel Booking Number exists for another date within this import."));
            return false;
        }

        return true;
    }

    public boolean isValidInsertion(List<BookingImportCache> list, BookingRow newElement) {
        if (list != null && !list.isEmpty()) {
            if (!list.get(0).getTransactionDate().equals(newElement.getTransactionDate())) {
                return true;
            }
        }

        return false;
    }

}
