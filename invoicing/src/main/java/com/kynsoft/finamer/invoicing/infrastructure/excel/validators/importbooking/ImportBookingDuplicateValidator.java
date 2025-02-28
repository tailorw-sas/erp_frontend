package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;

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
        if (obj.getHotelBookingNumber() == null) {
            return false;
        }
        try {
            if (manageHotelService.existByCode(InvoiceUtils.upperCaseAndTrim(obj.getManageHotelCode()))) {
                ManageHotelDto hotel = manageHotelService.findByCode(InvoiceUtils.upperCaseAndTrim(obj.getManageHotelCode()));
                if (service.existsByExactLastChars(this.removeBlankSpaces(obj.getHotelBookingNumber()), hotel.getId())) {
                    errorFieldList.add(new ErrorField("Hotel Booking Number", "Record has already been imported."));
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String removeBlankSpaces(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

}
